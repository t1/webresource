package com.github.t1.webresource;

import java.util.List;

import javax.annotation.processing.Messager;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

class WebResourceWriter {
    private final StringBuilder out = new StringBuilder();

    private final WebResourceType type;
    private final WebResourceField id;
    private final WebResourceField key;
    private final WebResourceField version;

    private int indent = 0;

    public WebResourceWriter(Messager messager, TypeElement typeElement) {
        this.type = new WebResourceType(typeElement);
        this.id = type.getIdField();
        this.key = type.getKeyField();
        if (id == null)
            messager.printMessage(Kind.ERROR, "can't find @Id or @WebResourceKey field", typeElement);
        this.version = type.getVersionField();
    }

    public String run() {
        if (key == null)
            throw new IllegalStateException("no id type found in " + type.qualified);
        append("package " + type.pkg + ";");
        nl();
        imports();
        nl();
        clazz();
        return out.toString();
    }

    private void imports() {
        if (id != null)
            imports(id.imports);
        if (requiresKeyTypeImport())
            imports(key.imports);
        append("import java.util.*;");
        nl();
        append("import javax.ejb.Stateless;");
        append("import javax.persistence.*;");
        nl();
        append("import javax.ws.rs.*;");
        append("import javax.ws.rs.core.*;");
        append("import javax.ws.rs.core.Response.*;");
        nl();
        append("import org.slf4j.*;");
    }

    private void imports(List<String> imports) {
        for (String imp : imports) {
            append("import " + imp + ";");
        }
    }

    private void append(String string) {
        appendIndent();
        out.append(string);
        nl();
    }

    private void appendIndent() {
        for (int i = 0; i < indent; i++) {
            out.append("    ");
        }
    }

    private void nl() {
        out.append('\n');
    }

    private boolean requiresKeyTypeImport() {
        return key != null && !key.imports.isEmpty() && !key.imports.equals(id.imports);
    }

    private void clazz() {
        path("/" + type.plural);
        append("@Stateless");
        append("public class " + type.simple + "WebResource {");
        ++indent;
        logger();
        nl();
        entityManager();
        nl();
        LIST();
        nl();
        GET();
        nl();
        if (!primary()) {
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

    private void entityManager() {
        append("@PersistenceContext" + (type.extended ? "(type = PersistenceContextType.EXTENDED)" : ""));
        append("private EntityManager em;");
    }

    private boolean primary() {
        return id.equals(key);
    }

    private void LIST() {
        append("@GET");
        append("public List<" + type.simple + "> list() {");
        ++indent;
        log("get all " + type.plural);
        nl();
        append("return em.createQuery(\"FROM " + type.entityName + " ORDER BY " + key.name + "\", " + type.simple
                + ".class).getResultList();");
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
        log("get " + type.lower + " {}", key.name);
        nl();
        findOrFail("result");
        evaluatePreconditions("result");
        nl();
        append("return Response.ok(result)" + etag("result") + ".build();");
        --indent;
        append("}");
    }

    private void findOrFail(String variableName) {
        String assignment = type.simple + " " + variableName + " = ";
        if (primary()) {
            append(assignment + "em.find(" + type.simple + ".class, " + key.name + ");");
        } else {
            append(assignment + "findByKey(" + key.name + ");");
        }
        append("if (" + variableName + " == null) {");
        ++indent;
        append("return Response.status(Status.NOT_FOUND).build();");
        --indent;
        append("}");
    }

    private String idParam() {
        return "@PathParam(\"id\") " + key.simpleType + " " + key.name;
    }

    private String etag(String var) {
        return (version == null) ? "" : ".tag(Objects.toString(" + var + "." + version.getter() + "()))";
    }

    private void findByKeyMethod() {
        append("private " + type.simple + " findByKey(" + key.simpleType + " " + key.name + ") {");
        ++indent;
        append("TypedQuery<" + type.simple + "> query = em.createQuery(\"FROM " + type.simple + " WHERE " + key.name
                + " = :" + key.name + "\", " + type.simple + ".class);");
        append("try {");
        ++indent;
        append("return query.setParameter(\"key\", " + key.name + ").getSingleResult();");
        --indent;
        append("} catch (NoResultException e) {");
        ++indent;
        append("return null;");
        --indent;
        append("}");
        --indent;
        append("}");
    }

    private void POST() {
        append("@POST");
        append("public Response create" + type.simple + "(" + type.simple + " " + type.lower
                + ", @Context UriInfo uriInfo) {");
        ++indent;
        log("post " + type.lower + " {}", type.lower);
        nl();
        append("if (" + type.lower + ".getId() == null) {");
        ++indent;
        append("em.persist(" + type.lower + ");");
        --indent;
        append("} else {");
        ++indent;
        append(type.lower + " = em.merge(" + type.lower + ");");
        --indent;
        append("}");
        append("em.flush();");
        nl();
        append("UriBuilder builder = uriInfo.getBaseUriBuilder();");
        append("builder.path(\"" + type.plural + "\").path(" + toString(type.lower + "." + key.getter() + "()") + ");");
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
        append("public Response update" + type.simple + "(" + idParam() + ", " + type.simple + " " + type.lower
                + requestContext() + ") {");
        ++indent;
        log("put " + type.lower + " " + key.name + " {}: {}", key.name, type.lower);
        nl();
        if (key.nullable) {
            append("if (" + type.lower + "." + key.getter() + "() == null) {");
            ++indent;
            append(type.lower + "." + key.setter() + "(" + key.name + ");");
            --indent;
            append("} else if (!" + type.lower + "." + key.getter() + "().equals(" + key.name + ")) {");
        } else {
            append("if (" + key.name + " != " + type.lower + "." + key.getter() + "()) {");
        }
        ++indent;
        append("String message = \"" + key.name + " conflict! path=\" + " + key.name + " + \", body=\" + " + type.lower
                + "." + key.getter() + "() + \".\\n\"");
        append("    + \"either leave the " + key.name + " in the body null or set it to the same " + key.name + "\";");
        append("return Response.status(Status.BAD_REQUEST).entity(message).build();");
        --indent;
        append("}");
        if (!primary()) {
            append("if (" + type.lower + "." + id.getter() + "() == null) {");
            ++indent;
            append(type.simple + " existing = findByKey(" + key.name + ");");
            append("if (existing == null) {");
            ++indent;
            append("return Response.status(Status.NOT_FOUND).build();");
            --indent;
            append("}");
            append(type.lower + "." + id.setter() + "(existing." + id.getter() + "());");
            if (version != null && version.nullable) {
                append("if (" + type.lower + "." + version.getter() + "() == null) {");
                ++indent;
                append(type.lower + "." + version.setter() + "(existing." + version.getter() + "());");
                --indent;
                append("}");
            }
            --indent;
            append("}");
        }
        evaluatePreconditions(type.lower);
        nl();
        append(type.simple + " result = em.merge(" + type.lower + ");");
        append("em.flush();");
        nl();
        append("if (result == null) {");
        ++indent;
        if (primary()) {
            append("return Response.status(Status.NOT_FOUND).build();");
        } else {
            append("throw new IllegalStateException(\"expected to be able to merge " + key.name + " \" + " + key.name
                    + ");");
        }
        --indent;
        append("}");
        append("return Response.ok(result)" + etag("result") + ".build();");
        --indent;
        append("}");
    }

    private void evaluatePreconditions(String entity) {
        if (version == null)
            return;
        nl();
        append("EntityTag eTag = new EntityTag(" + toString(entity + "." + version.getter() + "()") + ");");
        append("ResponseBuilder failed = request.evaluatePreconditions(eTag);");
        append("if (failed != null) {");
        ++indent;
        append("return failed.entity(" + entity + ").build();"); // etag is already set
        --indent;
        append("}");
    }

    private String requestContext() {
        return (version == null) ? "" : ", @Context Request request";
    }

    private void DELETE() {
        append("@DELETE");
        path("/{id}");
        append("public Response delete" + type.simple + "(" + idParam() + requestContext() + ") {");
        ++indent;
        log("delete " + type.lower + " {}", key.name);
        nl();
        findOrFail("result");
        evaluatePreconditions("result");
        nl();
        append("em.remove(result);");
        nl();
        append("return Response.ok(result)" + etag("result") + ".build();");
        --indent;
        append("}");
    }

    private void subresources() {
        for (WebResourceField subresource : type.getSubResourceFields()) {
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
        log("get " + subresource.name + " from " + type.lower + " {}", key.name);
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
        log("post " + subresource.name + " {} for " + type.lower + " {}", subresource.name, key.name);
        nl();
        findOrFail(type.lower);
        nl();
        append(type.lower + "." + subresource.getter() + "().add(" + subresource.name + ");");
        append("em.flush();");
        nl();
        append("UriBuilder builder = uriInfo.getBaseUriBuilder();");
        append("builder.path(\"" + type.plural + "\").path(" + toString(key.name) + ").path(\"" + subresource.name
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
        log("put " + subresource.name + " {} of " + type.lower + " {}", subresource.name, key.name);
        nl();
        findOrFail(type.lower);
        evaluatePreconditions(type.lower);
        nl();
        append(type.lower + "." + subresource.setter() + "(" + subresource.name + ");");
        append("em.flush();");
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
        log("delete " + subresource.name + " of " + type.lower + " {}", key.name);
        nl();
        findOrFail(type.lower);
        evaluatePreconditions(type.lower);
        nl();
        append(type.lower + "." + subresource.setter() + "(null);");
        append("em.flush();");
        nl();
        append("return Response.ok()" + etag(type.lower) + ".build();");
        --indent;
        append("}");
    }
}
