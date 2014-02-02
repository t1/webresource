package com.github.t1.webresource;

import javax.annotation.processing.Messager;
import javax.ejb.Stateless;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.ws.rs.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.t1.webresource.typewriter.*;

class WebResourceWriter extends IndentedWriter {
    private final WebResourceType type;
    private final JpaStoreWriter store;
    private final ClassBuilder typeWriter;

    public WebResourceWriter(Messager messager, TypeElement typeElement) {
        this.type = new WebResourceType(typeElement);
        if (type.id == null)
            messager.printMessage(Kind.ERROR, "can't find @Id or @WebResourceKey field", typeElement);
        this.typeWriter = new ClassBuilder(type.pkg, type.simple + "WebResource");
        this.store = new JpaStoreWriter(this, type, typeWriter);
    }

    public String run() {
        if (type.key == null)
            throw new IllegalStateException("no id type found in " + type.qualified);
        clazz();
        return out.toString();
    }

    private void clazz() {
        typeWriter.annotate(Path.class).value("/" + type.plural);
        typeWriter.annotate(Stateless.class);
        logger();
        store.declare();

        new ClassSourceWriter(typeWriter, this).write(type);

        LIST();
        println();
        GET();
        println();
        if (!type.primary()) {
            findByKeyMethod();
            println();
        }
        POST();
        println();
        PUT();
        println();
        DELETE();
        subresources();
        --indent;
        println("}");
    }

    private void path(String path) {
        println("@Path(\"" + path + "\")");
    }

    private void logger() {
        typeWriter.field(Logger.class, "log").final_().init(
                "LoggerFactory.getLogger(" + type.simple + "WebResource.class)").using(LoggerFactory.class);
    }

    private void LIST() {
        println("@GET");
        println("public Response list" + type.simple + "(@Context UriInfo uriInfo) {");
        ++indent;
        println("MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();");
        log("get " + type.plural + " where {}", "queryParams");
        println();
        store.list();
        println();
        println("return Response.ok(list).build();");
        --indent;
        println("}");
    }

    private void log(String message, String... args) {
        indent();
        out.append("log.debug(\"" + message + "\"");
        for (String arg : args) {
            out.append(", ").append(arg);
        }
        out.append(");");
        println();
    }

    private void GET() {
        println("@GET");
        path("/{id}");
        println("public Response get" + type.simple + "(" + idParam() + requestContext() + ") {");
        ++indent;
        log("get " + type.lower + " {}", type.key.name);
        println();
        findOrFail("result");
        evaluatePreconditions("result");
        println();
        println("return Response.ok(result)" + etag("result") + ".build();");
        --indent;
        println("}");
    }

    private void findOrFail(String variableName) {
        store.find(variableName);
        println("if (" + variableName + " == null) {");
        ++indent;
        println("return Response.status(Status.NOT_FOUND).build();");
        --indent;
        println("}");
    }

    private String idParam() {
        return "@PathParam(\"id\") " + type.key.simpleType + " " + type.key.name;
    }

    private String etag(String var) {
        return (type.version == null) ? "" : ".tag(Objects.toString(" + var + "." + type.version.getter() + "()))";
    }

    private void findByKeyMethod() {
        println("private " + type.simple + " findByKey(" + type.key.simpleType + " " + type.key.name + ") {");
        ++indent;
        store.findByKey();
        --indent;
        println("}");
    }

    private void POST() {
        println("@POST");
        println("public Response post" + type.simple + "(" + type.simple + " " + type.lower
                + ", @Context UriInfo uriInfo) {");
        ++indent;
        log("post " + type.lower + " {}", type.lower);
        println();
        store.persist();
        println();
        println("UriBuilder builder = uriInfo.getBaseUriBuilder();");
        println("builder.path(\"" + type.plural + "\").path(" + toString(type.lower + "." + type.key.getter() + "()")
                + ");");
        println("return Response.created(builder.build())" + etag(type.lower) + ".build();");
        --indent;
        println("}");
    }

    private String toString(String name) {
        return "Objects.toString(" + name + ")";
    }

    private void PUT() {
        println("@PUT");
        path("/{id}");
        println("public Response put" + type.simple + "(" + idParam() + ", " + type.simple + " " + type.lower
                + requestContext() + ") {");
        ++indent;
        log("put " + type.lower + " " + type.key.name + " {}: {}", type.key.name, type.lower);
        println();
        if (type.key.nullable) {
            println("if (" + type.lower + "." + type.key.getter() + "() == null) {");
            ++indent;
            println(type.lower + "." + type.key.setter() + "(" + type.key.name + ");");
            --indent;
            println("} else if (!" + type.lower + "." + type.key.getter() + "().equals(" + type.key.name + ")) {");
        } else {
            println("if (" + type.key.name + " != " + type.lower + "." + type.key.getter() + "()) {");
        }
        ++indent;
        println("String message = \"" + type.key.name + " conflict! path=\" + " + type.key.name + " + \", body=\" + "
                + type.lower + "." + type.key.getter() + "() + \".\\n\"");
        println("    + \"either leave the " + type.key.name + " in the body null or set it to the same " + type.key.name
                + "\";");
        println("return Response.status(Status.BAD_REQUEST).entity(message).build();");
        --indent;
        println("}");
        if (!type.primary()) {
            println("if (" + type.lower + "." + type.id.getter() + "() == null) {");
            ++indent;
            println(type.simple + " existing = findByKey(" + type.key.name + ");");
            println("if (existing == null) {");
            ++indent;
            println("return Response.status(Status.NOT_FOUND).build();");
            --indent;
            println("}");
            println(type.lower + "." + type.id.setter() + "(existing." + type.id.getter() + "());");
            if (type.version != null && type.version.nullable) {
                println("if (" + type.lower + "." + type.version.getter() + "() == null) {");
                ++indent;
                println(type.lower + "." + type.version.setter() + "(existing." + type.version.getter() + "());");
                --indent;
                println("}");
            }
            --indent;
            println("}");
        }
        evaluatePreconditions(type.lower);
        println();
        store.merge();
        println();
        println("if (result == null) {");
        ++indent;
        if (type.primary()) {
            println("return Response.status(Status.NOT_FOUND).build();");
        } else {
            println("throw new IllegalStateException(\"expected to be able to merge " + type.key.name + " \" + "
                    + type.key.name + ");");
        }
        --indent;
        println("}");
        println("return Response.ok(result)" + etag("result") + ".build();");
        --indent;
        println("}");
    }

    private void evaluatePreconditions(String entity) {
        if (type.version == null)
            return;
        println();
        println("EntityTag eTag = new EntityTag(" + toString(entity + "." + type.version.getter() + "()") + ");");
        println("ResponseBuilder failed = request.evaluatePreconditions(eTag);");
        println("if (failed != null) {");
        ++indent;
        println("return failed.entity(" + entity + ").build();"); // etag is already set
        --indent;
        println("}");
    }

    private String requestContext() {
        return (type.version == null) ? "" : ", @Context Request request";
    }

    private void DELETE() {
        println("@DELETE");
        path("/{id}");
        println("public Response delete" + type.simple + "(" + idParam() + requestContext() + ") {");
        ++indent;
        log("delete " + type.lower + " {}", type.key.name);
        println();
        findOrFail("result");
        evaluatePreconditions("result");
        println();
        store.remove();
        println();
        println("return Response.ok(result)" + etag("result") + ".build();");
        --indent;
        println("}");
    }

    private void subresources() {
        for (WebResourceField subresource : type.subResourceFields) {
            println();
            subGET(subresource);
            if (subresource.isCollection) {
                println();
                subPOST(subresource);
            }
            println();
            subPUT(subresource);
            if (subresource.nullable) {
                println();
                subDELETE(subresource);
            }
        }
    }

    private void subGET(WebResourceField subresource) {
        println("@GET");
        path("/{id}/" + subresource.name);
        println("public Response get" + type.simple + subresource.uppercaps() + "(" + idParam() + requestContext()
                + ") {");
        ++indent;
        log("get " + subresource.name + " from " + type.lower + " {}", type.key.name);
        println();
        findOrFail("result");
        evaluatePreconditions("result");
        println();
        println("return Response.ok(result." + subresource.getter() + "())" + etag("result") + ".build();");
        --indent;
        println("}");
    }

    private void subPOST(WebResourceField subresource) {
        println("@POST");
        path("/{id}/" + subresource.name);
        println("public Response add" + type.simple + subresource.uppercaps() + "(" + idParam() + ", "
                + subresource.uncollectedType + " " + subresource.name + ", @Context UriInfo uriInfo) {");
        ++indent;
        log("post " + subresource.name + " {} for " + type.lower + " {}", subresource.name, type.key.name);
        println();
        findOrFail(type.lower);
        println();
        println(type.lower + "." + subresource.getter() + "().add(" + subresource.name + ");");
        store.flush();
        println();
        println("UriBuilder builder = uriInfo.getBaseUriBuilder();");
        println("builder.path(\"" + type.plural + "\").path(" + toString(type.key.name) + ").path(\"" + subresource.name
                + "\").path(" + toString(subresource.name) + ");");
        println("return Response.created(builder.build()).build();");
        --indent;
        println("}");
    }

    private void subPUT(WebResourceField subresource) {
        println("@PUT");
        path("/{id}/" + subresource.name);
        println("public Response update" + type.simple + subresource.uppercaps() + "(" + idParam() + requestContext()
                + ", " + subresource.simpleType + " " + subresource.name + ") {");
        ++indent;
        log("put " + subresource.name + " {} of " + type.lower + " {}", subresource.name, type.key.name);
        println();
        findOrFail(type.lower);
        evaluatePreconditions(type.lower);
        println();
        println(type.lower + "." + subresource.setter() + "(" + subresource.name + ");");
        store.flush();
        println();
        println("return Response.ok(" + subresource.name + ")" + etag(type.lower) + ".build();");
        --indent;
        println("}");
    }

    private void subDELETE(WebResourceField subresource) {
        println("@DELETE");
        path("/{id}/" + subresource.name);
        println("public Response delete" + type.simple + subresource.uppercaps() + "(" + idParam() + requestContext()
                + ") {");
        ++indent;
        log("delete " + subresource.name + " of " + type.lower + " {}", type.key.name);
        println();
        findOrFail(type.lower);
        evaluatePreconditions(type.lower);
        println();
        println(type.lower + "." + subresource.setter() + "(null);");
        store.flush();
        println();
        println("return Response.ok()" + etag(type.lower) + ".build();");
        --indent;
        println("}");
    }
}
