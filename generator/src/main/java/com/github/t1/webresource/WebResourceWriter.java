package com.github.t1.webresource;

import java.util.List;

import javax.annotation.processing.Messager;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

class WebResourceWriter extends AbstractWriter {
    private final WebResourceType type;
    private final JpaStoreWriter store;

    public WebResourceWriter(Messager messager, TypeElement typeElement) {
        this.type = new WebResourceType(typeElement);
        if (type.id == null)
            messager.printMessage(Kind.ERROR, "can't find @Id or @WebResourceKey field", typeElement);
        this.store = new JpaStoreWriter(this, type);
    }

    public String run() {
        if (type.key == null)
            throw new IllegalStateException("no id type found in " + type.qualified);
        append("package " + type.pkg + ";");
        nl();
        imports();
        nl();
        clazz();
        return out.toString();
    }

    private void imports() {
        if (type.id != null)
            imports(type.id.imports);
        if (requiresKeyTypeImport())
            imports(type.key.imports);
        append("import java.util.*;");
        nl();
        append("import javax.ejb.Stateless;");
        append("import javax.persistence.*;");
        append("import javax.persistence.criteria.*;");
        append("import javax.ws.rs.*;");
        append("import javax.ws.rs.Path;");
        append("import javax.ws.rs.core.*;");
        append("import javax.ws.rs.core.Response.Status;");
        if (type.version != null)
            append("import javax.ws.rs.core.Response.ResponseBuilder;");
        nl();
        append("import org.slf4j.*;");
    }

    private void imports(List<String> imports) {
        for (String imp : imports) {
            append("import " + imp + ";");
        }
    }

    private boolean requiresKeyTypeImport() {
        return type.key != null && !type.key.imports.isEmpty() && !type.key.imports.equals(type.id.imports);
    }

    private void clazz() {
        path("/" + type.plural);
        append("@Stateless");
        append("public class " + type.simple + "WebResource {");
        ++indent;
        logger();
        nl();
        store.declare();
        nl();
        LIST();
        nl();
        GET();
        nl();
        if (!type.primary()) {
            findByKeyMethod();
            nl();
        }
        POST();
        nl();
        PUT();
        nl();
        DELETE();
        subresources();
        --indent;
        append("}");
    }

    private void path(String path) {
        append("@Path(\"" + path + "\")");
    }

    private void logger() {
        append("private final Logger log = LoggerFactory.getLogger(" + type.simple + "WebResource.class);");
    }

    private void LIST() {
        append("@GET");
        append("public Response list" + type.simple + "(@Context UriInfo uriInfo) {");
        ++indent;
        append("MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();");
        log("get " + type.plural + " where {}", "queryParams");
        nl();
        store.list();
        nl();
        append("return Response.ok(list).build();");
        --indent;
        append("}");
    }

    private void log(String message, String... args) {
        appendIndent();
        out.append("log.debug(\"" + message + "\"");
        for (String arg : args) {
            out.append(", ").append(arg);
        }
        out.append(");");
        nl();
    }

    private void GET() {
        append("@GET");
        path("/{id}");
        append("public Response get" + type.simple + "(" + idParam() + requestContext() + ") {");
        ++indent;
        log("get " + type.lower + " {}", type.key.name);
        nl();
        findOrFail("result");
        evaluatePreconditions("result");
        nl();
        append("return Response.ok(result)" + etag("result") + ".build();");
        --indent;
        append("}");
    }

    private void findOrFail(String variableName) {
        store.find(variableName);
        append("if (" + variableName + " == null) {");
        ++indent;
        append("return Response.status(Status.NOT_FOUND).build();");
        --indent;
        append("}");
    }

    private String idParam() {
        return "@PathParam(\"id\") " + type.key.simpleType + " " + type.key.name;
    }

    private String etag(String var) {
        return (type.version == null) ? "" : ".tag(Objects.toString(" + var + "." + type.version.getter() + "()))";
    }

    private void findByKeyMethod() {
        append("private " + type.simple + " findByKey(" + type.key.simpleType + " " + type.key.name + ") {");
        ++indent;
        store.findByKey();
        --indent;
        append("}");
    }

    private void POST() {
        append("@POST");
        append("public Response post" + type.simple + "(" + type.simple + " " + type.lower
                + ", @Context UriInfo uriInfo) {");
        ++indent;
        log("post " + type.lower + " {}", type.lower);
        nl();
        store.persist();
        nl();
        append("UriBuilder builder = uriInfo.getBaseUriBuilder();");
        append("builder.path(\"" + type.plural + "\").path(" + toString(type.lower + "." + type.key.getter() + "()")
                + ");");
        append("return Response.created(builder.build())" + etag(type.lower) + ".build();");
        --indent;
        append("}");
    }

    private String toString(String name) {
        return "Objects.toString(" + name + ")";
    }

    private void PUT() {
        append("@PUT");
        path("/{id}");
        append("public Response put" + type.simple + "(" + idParam() + ", " + type.simple + " " + type.lower
                + requestContext() + ") {");
        ++indent;
        log("put " + type.lower + " " + type.key.name + " {}: {}", type.key.name, type.lower);
        nl();
        if (type.key.nullable) {
            append("if (" + type.lower + "." + type.key.getter() + "() == null) {");
            ++indent;
            append(type.lower + "." + type.key.setter() + "(" + type.key.name + ");");
            --indent;
            append("} else if (!" + type.lower + "." + type.key.getter() + "().equals(" + type.key.name + ")) {");
        } else {
            append("if (" + type.key.name + " != " + type.lower + "." + type.key.getter() + "()) {");
        }
        ++indent;
        append("String message = \"" + type.key.name + " conflict! path=\" + " + type.key.name + " + \", body=\" + "
                + type.lower + "." + type.key.getter() + "() + \".\\n\"");
        append("    + \"either leave the " + type.key.name + " in the body null or set it to the same " + type.key.name
                + "\";");
        append("return Response.status(Status.BAD_REQUEST).entity(message).build();");
        --indent;
        append("}");
        if (!type.primary()) {
            append("if (" + type.lower + "." + type.id.getter() + "() == null) {");
            ++indent;
            append(type.simple + " existing = findByKey(" + type.key.name + ");");
            append("if (existing == null) {");
            ++indent;
            append("return Response.status(Status.NOT_FOUND).build();");
            --indent;
            append("}");
            append(type.lower + "." + type.id.setter() + "(existing." + type.id.getter() + "());");
            if (type.version != null && type.version.nullable) {
                append("if (" + type.lower + "." + type.version.getter() + "() == null) {");
                ++indent;
                append(type.lower + "." + type.version.setter() + "(existing." + type.version.getter() + "());");
                --indent;
                append("}");
            }
            --indent;
            append("}");
        }
        evaluatePreconditions(type.lower);
        nl();
        store.merge();
        nl();
        append("if (result == null) {");
        ++indent;
        if (type.primary()) {
            append("return Response.status(Status.NOT_FOUND).build();");
        } else {
            append("throw new IllegalStateException(\"expected to be able to merge " + type.key.name + " \" + "
                    + type.key.name + ");");
        }
        --indent;
        append("}");
        append("return Response.ok(result)" + etag("result") + ".build();");
        --indent;
        append("}");
    }

    private void evaluatePreconditions(String entity) {
        if (type.version == null)
            return;
        nl();
        append("EntityTag eTag = new EntityTag(" + toString(entity + "." + type.version.getter() + "()") + ");");
        append("ResponseBuilder failed = request.evaluatePreconditions(eTag);");
        append("if (failed != null) {");
        ++indent;
        append("return failed.entity(" + entity + ").build();"); // etag is already set
        --indent;
        append("}");
    }

    private String requestContext() {
        return (type.version == null) ? "" : ", @Context Request request";
    }

    private void DELETE() {
        append("@DELETE");
        path("/{id}");
        append("public Response delete" + type.simple + "(" + idParam() + requestContext() + ") {");
        ++indent;
        log("delete " + type.lower + " {}", type.key.name);
        nl();
        findOrFail("result");
        evaluatePreconditions("result");
        nl();
        store.remove();
        nl();
        append("return Response.ok(result)" + etag("result") + ".build();");
        --indent;
        append("}");
    }

    private void subresources() {
        for (WebResourceField subresource : type.subResourceFields) {
            nl();
            subGET(subresource);
            if (subresource.isCollection) {
                nl();
                subPOST(subresource);
            }
            nl();
            subPUT(subresource);
            if (subresource.nullable) {
                nl();
                subDELETE(subresource);
            }
        }
    }

    private void subGET(WebResourceField subresource) {
        append("@GET");
        path("/{id}/" + subresource.name);
        append("public Response get" + type.simple + subresource.uppercaps() + "(" + idParam() + requestContext()
                + ") {");
        ++indent;
        log("get " + subresource.name + " from " + type.lower + " {}", type.key.name);
        nl();
        findOrFail("result");
        evaluatePreconditions("result");
        nl();
        append("return Response.ok(result." + subresource.getter() + "())" + etag("result") + ".build();");
        --indent;
        append("}");
    }

    private void subPOST(WebResourceField subresource) {
        append("@POST");
        path("/{id}/" + subresource.name);
        append("public Response add" + type.simple + subresource.uppercaps() + "(" + idParam() + ", "
                + subresource.uncollectedType + " " + subresource.name + ", @Context UriInfo uriInfo) {");
        ++indent;
        log("post " + subresource.name + " {} for " + type.lower + " {}", subresource.name, type.key.name);
        nl();
        findOrFail(type.lower);
        nl();
        append(type.lower + "." + subresource.getter() + "().add(" + subresource.name + ");");
        store.flush();
        nl();
        append("UriBuilder builder = uriInfo.getBaseUriBuilder();");
        append("builder.path(\"" + type.plural + "\").path(" + toString(type.key.name) + ").path(\"" + subresource.name
                + "\").path(" + toString(subresource.name) + ");");
        append("return Response.created(builder.build()).build();");
        --indent;
        append("}");
    }

    private void subPUT(WebResourceField subresource) {
        append("@PUT");
        path("/{id}/" + subresource.name);
        append("public Response update" + type.simple + subresource.uppercaps() + "(" + idParam() + requestContext()
                + ", " + subresource.simpleType + " " + subresource.name + ") {");
        ++indent;
        log("put " + subresource.name + " {} of " + type.lower + " {}", subresource.name, type.key.name);
        nl();
        findOrFail(type.lower);
        evaluatePreconditions(type.lower);
        nl();
        append(type.lower + "." + subresource.setter() + "(" + subresource.name + ");");
        store.flush();
        nl();
        append("return Response.ok(" + subresource.name + ")" + etag(type.lower) + ".build();");
        --indent;
        append("}");
    }

    private void subDELETE(WebResourceField subresource) {
        append("@DELETE");
        path("/{id}/" + subresource.name);
        append("public Response delete" + type.simple + subresource.uppercaps() + "(" + idParam() + requestContext()
                + ") {");
        ++indent;
        log("delete " + subresource.name + " of " + type.lower + " {}", type.key.name);
        nl();
        findOrFail(type.lower);
        evaluatePreconditions(type.lower);
        nl();
        append(type.lower + "." + subresource.setter() + "(null);");
        store.flush();
        nl();
        append("return Response.ok()" + etag(type.lower) + ".build();");
        --indent;
        append("}");
    }
}
