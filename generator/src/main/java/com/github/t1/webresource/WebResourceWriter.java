package com.github.t1.webresource;

import com.github.t1.webresource.typewriter.*;
import org.slf4j.*;

import javax.annotation.processing.Messager;
import javax.ejb.Stateless;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.PrintWriter;

class WebResourceWriter {
    private final WebResourceType type;
    private JpaStoreWriter store;
    private ClassBuilder classBuilder;

    public WebResourceWriter(Messager messager, TypeElement typeElement) {
        this.type = new WebResourceType(typeElement);
        if (type.id == null)
            messager.printMessage(Kind.ERROR, "can't find @Id or @WebResourceKey field", typeElement);
        if (type.key == null)
            throw new IllegalStateException("no id type found in " + type.qualified);
    }

    synchronized public String run() {
        this.store = new JpaStoreWriter(type);
        this.classBuilder = new ClassBuilder(type.pkg, type.simple + "WebResource");
        buildClass();
        return new ClassSourceWriter(classBuilder, type).write();
    }

    private void buildClass() {
        classBuilder.annotate(Path.class).value("/" + type.plural);
        classBuilder.annotate(Stateless.class);
        logger();
        store.declare(classBuilder);

        LIST();
        GET();
        if (!type.primary())
            findByKeyMethod();
        POST();
        PUT();
        DELETE();
        subresources();
    }

    private void logger() {
        classBuilder.field(Logger.class, "log").final_().init(
                "LoggerFactory.getLogger(" + type.simple + "WebResource.class)").using(LoggerFactory.class);
    }

    private void LIST() {
        MethodBuilder method = classBuilder.method(Response.class, "list" + type.simple);
        method.annotate(GET.class);
        uriInfoParameter(method);
        try (PrintWriter body = method.body()) {
            body.println("MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();");
            body.println(logLine("get " + type.plural + " where {}", "queryParams"));
            body.println();
            store.list(body);
            body.println(
                    "GenericEntity<List<" + type.simple + ">> genericEntity = new GenericEntity<List<" + type.simple
                            + ">>(list) {};");
            body.println();
            body.println("return Response.ok(genericEntity).build();");
        }
    }

    private String logLine(String message, String... args) {
        StringBuilder line = new StringBuilder();
        line.append("log.debug(\"").append(message).append("\"");
        for (String arg : args) {
            line.append(", ").append(arg);
        }
        line.append(");");
        return line.toString();
    }

    private void GET() {
        MethodBuilder method = classBuilder.method(Response.class, "get" + type.simple);
        method.annotate(GET.class);
        idParameter(method);
        requestContextParameter(method);
        try (PrintWriter body = method.body()) {
            body.println(logLine("get " + type.lower + " {}", type.key.name));
            body.println();
            findOrFail(body, "result");
            evaluatePreconditions(body, "result");
            body.println();
            body.println("return Response.ok(result)" + etag("result") + ".build();");
        }
    }

    private void requestContextParameter(MethodBuilder method) {
        if (type.version != null) {
            method.parameter(Request.class, "request").annotate(Context.class);
        }
    }

    private void findOrFail(PrintWriter body, String variableName) {
        store.find(body, variableName);
        body.println("if (" + variableName + " == null) {");
        body.println("    return Response.status(Status.NOT_FOUND).build();");
        body.println("}");
    }

    private String etag(String var) {
        return (type.version == null) ? "" : ".tag(Objects.toString(" + var + "." + type.version.getter() + "()))";
    }

    private void findByKeyMethod() {
        MethodBuilder method = classBuilder.method(type.type, "findByKey").private_();
        method.parameter(type.key.type, type.key.name);
        store.findByKey(method.body());
    }

    private void POST() {
        MethodBuilder method = classBuilder.method(Response.class, "post" + type.simple);
        method.annotate(POST.class);
        typeParameter(method);
        uriInfoParameter(method);

        try (PrintWriter body = method.body()) {
            body.println(logLine("post " + type.lower + " {}", type.lower));
            body.println();
            store.persist(body);
            body.println();
            body.println("UriBuilder builder = uriInfo.getBaseUriBuilder();");
            body.println("builder.path(\"" + type.plural + "\").path("
                    + toString(type.lower + "." + type.key.getter() + "()") + ");");
            body.println("return Response.created(builder.build())" + etag(type.lower) + ".build();");
        }
    }

    private void typeParameter(MethodBuilder method) {
        method.parameter(type.type, type.lower);
    }

    private AnnotationBuilder uriInfoParameter(MethodBuilder method) {
        return method.parameter(UriInfo.class, "uriInfo").annotate(Context.class);
    }

    private void idParameter(MethodBuilder method) {
        idParameterWithKey(method, "/{id}");
    }

    private void idParameter(MethodBuilder method, String subresource) {
        idParameterWithKey(method, "/{id}/" + subresource);
    }

    private void idParameterWithKey(MethodBuilder method, String key) {
        method.annotate(Path.class).value(key);
        method.parameter(type.key.type, type.key.name).annotate(PathParam.class).value("id");
    }

    private String toString(String name) {
        return "Objects.toString(" + name + ")";
    }

    private void PUT() {
        MethodBuilder method = classBuilder.method(Response.class, "put" + type.simple);
        method.annotate(PUT.class);
        idParameter(method);
        typeParameter(method);
        requestContextParameter(method);
        try (PrintWriter body = method.body()) {
            body.println(logLine("put " + type.lower + " " + type.key.name + " {}: {}", type.key.name, type.lower));
            body.println();
            if (type.key.type.nullable) {
                body.println("if (" + type.lower + "." + type.key.getter() + "() == null) {");
                body.println("    " + type.lower + "." + type.key.setter() + "(" + type.key.name + ");");
                body.println("} else if (!" + type.lower + "." + type.key.getter() + "().equals(" + type.key.name
                        + ")) {");
            } else {
                body.println("if (" + type.key.name + " != " + type.lower + "." + type.key.getter() + "()) {");
            }
            body.println("    String message = \"" + type.key.name + " conflict! path=\" + " + type.key.name
                    + " + \", body=\" + " + type.lower + "." + type.key.getter() + "() + \".\\n\"");
            body.println("        + \"either leave the " + type.key.name + " in the body null or set it to the same "
                    + type.key.name + "\";");
            body.println("    return Response.status(Status.BAD_REQUEST).entity(message).build();");
            body.println("}");
            if (!type.primary()) {
                body.println("if (" + type.lower + "." + type.id.getter() + "() == null) {");
                body.println("    " + type.simple + " existing = findByKey(" + type.key.name + ");");
                body.println("    if (existing == null) {");
                body.println("        return Response.status(Status.NOT_FOUND).build();");
                body.println("    }");
                body.println("    " + type.lower + "." + type.id.setter() + "(existing." + type.id.getter() + "());");
                if (type.version != null && type.version.type.nullable) {
                    body.println("    if (" + type.lower + "." + type.version.getter() + "() == null) {");
                    body.println("        " + type.lower + "." + type.version.setter() + "(existing."
                            + type.version.getter() + "());");
                    body.println("    }");
                }
                body.println("}");
            }
            evaluatePreconditions(body, type.lower);
            body.println();
            store.merge(body);
            body.println();
            body.println("if (result == null) {");
            if (type.primary()) {
                body.println("    return Response.status(Status.NOT_FOUND).build();");
            } else {
                body.println("    throw new IllegalStateException(\"expected to be able to merge " + type.key.name
                        + " \" + " + type.key.name + ");");
            }
            body.println("}");
            body.println("return Response.ok(result)" + etag("result") + ".build();");
        }
    }

    private void evaluatePreconditions(PrintWriter out, String entity) {
        if (type.version == null)
            return;
        out.println();
        out.println("EntityTag eTag = new EntityTag(" + toString(entity + "." + type.version.getter() + "()") + ");");
        out.println("ResponseBuilder failed = request.evaluatePreconditions(eTag);");
        out.println("if (failed != null) {");
        out.println("    return failed.entity(" + entity + ").build();"); // etag is already set
        out.println("}");
    }

    private void DELETE() {
        MethodBuilder method = classBuilder.method(Response.class, "delete" + type.simple);
        method.annotate(DELETE.class);
        idParameter(method);
        requestContextParameter(method);
        try (PrintWriter body = method.body()) {
            body.println(logLine("delete " + type.lower + " {}", type.key.name));
            body.println();
            findOrFail(body, "result");
            evaluatePreconditions(body, "result");
            body.println();
            store.remove(body);
            body.println();
            body.println("return Response.ok(result)" + etag("result") + ".build();");
        }
    }

    private void subresources() {
        for (WebResourceField subresource : type.subResourceFields) {
            subGET(subresource);
            if (subresource.type.isCollection) {
                subPOST(subresource);
            }
            subPUT(subresource);
            if (subresource.type.nullable) {
                subDELETE(subresource);
            }
        }
    }

    private void subGET(WebResourceField subresource) {
        MethodBuilder method = classBuilder.method(Response.class, "get" + type.simple + subresource.uppercaps());
        method.annotate(GET.class);
        idParameter(method, subresource.name);
        requestContextParameter(method);
        try (PrintWriter body = method.body()) {
            body.println(logLine("get " + subresource.name + " from " + type.lower + " {}", type.key.name));
            body.println();
            findOrFail(body, "result");
            evaluatePreconditions(body, "result");
            body.println();
            body.println("return Response.ok(result." + subresource.getter() + "())" + etag("result") + ".build();");
        }
    }

    private void subPOST(WebResourceField subresource) {
        MethodBuilder method = classBuilder.method(Response.class, "add" + type.simple + subresource.uppercaps());
        method.annotate(POST.class);
        idParameter(method, subresource.name);
        method.parameter(subresource.type.uncollected, subresource.name);
        uriInfoParameter(method);
        try (PrintWriter body = method.body()) {
            body.println(logLine("post " + subresource.name + " {} for " + type.lower + " {}", subresource.name,
                    type.key.name));
            body.println();
            findOrFail(body, type.lower);
            body.println();
            body.println(type.lower + "." + subresource.getter() + "().add(" + subresource.name + ");");
            store.flush(body);
            body.println();
            body.println("UriBuilder builder = uriInfo.getBaseUriBuilder();");
            body.println("builder.path(\"" + type.plural + "\").path(" + toString(type.key.name) + ").path(\""
                    + subresource.name + "\").path(" + toString(subresource.name) + ");");
            body.println("return Response.created(builder.build()).build();");
        }
    }

    private void subPUT(WebResourceField subresource) {
        MethodBuilder method = classBuilder.method(Response.class, "update" + type.simple + subresource.uppercaps());
        method.annotate(PUT.class);
        idParameter(method, subresource.name);
        requestContextParameter(method);
        ParameterBuilder parameter = method.parameter(subresource.type, subresource.name);
        if (subresource.type.isCollection)
            parameter.generic(subresource.type.uncollected.simple);
        try (PrintWriter body = method.body()) {
            body.println(logLine("put " + subresource.name + " {} of " + type.lower + " {}", subresource.name,
                    type.key.name));
            body.println();
            findOrFail(body, type.lower);
            evaluatePreconditions(body, type.lower);
            body.println();
            body.println(type.lower + "." + subresource.setter() + "(" + subresource.name + ");");
            store.flush(body);
            body.println();
            body.println("return Response.ok(" + subresource.name + ")" + etag(type.lower) + ".build();");
        }
    }

    private void subDELETE(WebResourceField subresource) {
        MethodBuilder method = classBuilder.method(Response.class, "delete" + type.simple + subresource.uppercaps());
        method.annotate(DELETE.class);
        idParameter(method, subresource.name);
        requestContextParameter(method);
        try (PrintWriter body = method.body()) {
            body.println(logLine("delete " + subresource.name + " of " + type.lower + " {}", type.key.name));
            body.println();
            findOrFail(body, type.lower);
            evaluatePreconditions(body, type.lower);
            body.println();
            body.println(type.lower + "." + subresource.setter() + "(null);");
            store.flush(body);
            body.println();
            body.println("return Response.ok()" + etag(type.lower) + ".build();");
        }
    }
}
