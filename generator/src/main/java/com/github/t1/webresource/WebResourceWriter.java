package com.github.t1.webresource;

import java.util.List;

import javax.annotation.processing.Messager;
import javax.lang.model.element.*;
import javax.tools.Diagnostic.Kind;

class WebResourceWriter {
    private final StringBuilder out = new StringBuilder();

    private final TypeElement type;
    private final String pkg;
    private final String simple;
    private final String lower;
    private final String plural;
    private final WebResourceField id;
    private final WebResourceField key;
    private final WebResourceField version;
    private final boolean extended;

    private int indent = 0;

    public WebResourceWriter(Messager messager, TypeElement type) {
        this.type = type;
        this.pkg = pkg();
        this.simple = type.getSimpleName().toString();
        this.lower = simple.toLowerCase();
        this.plural = plural(lower);
        this.id = getIdField(type);
        this.key = getKeyField();
        if (id == null)
            messager.printMessage(Kind.ERROR, "can't find @Id or @WebResourceKey field", type);
        this.version = getVersionField(type);
        this.extended = isExtended(type);
    }

    private WebResourceField getIdField(TypeElement type) {
        // don't use the Id type itself, it may not be available at compile-time
        return WebResourceField.findField(type, "javax.persistence.Id");
    }

    private WebResourceField getKeyField() {
        WebResourceField keyField = WebResourceField.findField(type, WebResourceKey.class.getName());
        return (keyField == null) ? id : keyField;
    }

    private WebResourceField getVersionField(TypeElement type) {
        // don't use the Version type itself, it may not be available at compile-time
        return WebResourceField.findField(type, "javax.persistence.Version");
    }

    private String pkg() {
        for (Element element = type; element != null; element = element.getEnclosingElement()) {
            if (ElementKind.PACKAGE == element.getKind()) {
                return ((PackageElement) element).getQualifiedName().toString();
            }
        }
        throw new IllegalStateException("no package for " + type);
    }

    private String plural(String name) {
        if (name.endsWith("y"))
            return name.substring(0, name.length() - 1) + "ies";
        return name + "s";
    }

    private boolean isExtended(TypeElement type) {
        WebResource annotation = type.getAnnotation(WebResource.class);
        if (annotation == null)
            throw new RuntimeException("expected type to be annotated as WebResource: " + type);
        return annotation.extended();
    }

    public String run() {
        if (key == null)
            throw new IllegalStateException("no id type found in " + type.getQualifiedName());
        append("package " + pkg + ";");
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
        path("/");
        append("@Stateless");
        append("public class " + simple + "WebResource {");
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

    private boolean primary() {
        return id.equals(key);
    }

    private void path(String path) {
        append("@Path(\"" + path + "\")");
    }

    private void logger() {
        append("private final Logger log = LoggerFactory.getLogger(" + simple + "WebResource.class);");
    }

    private void entityManager() {
        append("@PersistenceContext" + (extended ? "(type = PersistenceContextType.EXTENDED)" : ""));
        append("private EntityManager em;");
    }

    private void LIST() {
        append("@GET");
        path(plural);
        append("public List<" + simple + "> list() {");
        ++indent;
        log("get all " + plural);
        nl();
        append("return em.createQuery(\"FROM " + simple + " ORDER BY " + key.name + "\", " + simple
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
        idPath();
        append("public Response get" + simple + "(@PathParam(\"id\") " + key.simpleType + " " + key.name
                + requestContext() + ") {");
        ++indent;
        log("get " + lower + " {}", key.name);
        nl();
        if (primary()) {
            append(simple + " result = em.find(" + simple + ".class, " + key.name + ");");
        } else {
            append(simple + " result = findByKey(" + key.name + ");");
        }
        append("if (result == null) {");
        ++indent;
        append("return Response.status(Status.NOT_FOUND).build();");
        --indent;
        append("}");
        evaluatePreconditions("result");
        nl();
        append("return Response.ok(result)" + etag("result") + ".build();");
        --indent;
        append("}");
    }

    private String etag(String var) {
        return (version == null) ? "" : ".tag(Objects.toString(" + var + "." + version.getter() + "()))";
    }

    private void findByKeyMethod() {
        append("private " + simple + " findByKey(" + key.simpleType + " " + key.name + ") {");
        ++indent;
        append("TypedQuery<" + simple + "> query = em.createQuery(\"FROM " + simple + " WHERE " + key.name + " = :"
                + key.name + "\", " + simple + ".class);");
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

    private void idPath() {
        path(plural + "/{id}");
    }

    private void POST() {
        append("@POST");
        path(plural);
        append("public Response create" + simple + "(" + simple + " " + lower + ", @Context UriInfo uriInfo) {");
        ++indent;
        log("post " + lower + " {}", lower);
        nl();
        append("em.persist(" + lower + ");");
        append("em.flush();");
        nl();
        append("UriBuilder builder = uriInfo.getBaseUriBuilder();");
        append("builder.path(\"" + plural + "\").path(\"\" + " + lower + "." + key.getter() + "());");
        append("return Response.created(builder.build())" + etag(lower) + ".build();");
        --indent;
        append("}");
    }

    private void PUT() {
        append("@PUT");
        idPath();
        append("public Response update" + simple + "(@PathParam(\"id\") " + key.simpleType + " " + key.name + ", "
                + simple + " " + lower + requestContext() + ") {");
        ++indent;
        log("put " + lower + " " + key.name + " {}: {}", key.name, lower);
        nl();
        if (key.nullable) {
            append("if (" + lower + "." + key.getter() + "() == null) {");
            ++indent;
            append(lower + "." + key.setter() + "(" + key.name + ");");
            --indent;
            append("} else if (!" + lower + "." + key.getter() + "().equals(" + key.name + ")) {");
        } else {
            append("if (" + key.name + " != " + lower + "." + key.getter() + "()) {");
        }
        ++indent;
        append("String message = \"" + key.name + " conflict! path=\" + " + key.name + " + \", body=\" + " + lower
                + "." + key.getter() + "() + \".\\n\"");
        append("    + \"either leave the " + key.name + " in the body null or set it to the same " + key.name + "\";");
        append("return Response.status(Status.BAD_REQUEST).entity(message).build();");
        --indent;
        append("}");
        if (!primary()) {
            append("if (" + lower + "." + id.getter() + "() == null) {");
            ++indent;
            append(simple + " existing = findByKey(" + key.name + ");");
            append("if (existing == null) {");
            ++indent;
            append("return Response.status(Status.NOT_FOUND).build();");
            --indent;
            append("}");
            append(lower + "." + id.setter() + "(existing." + id.getter() + "());");
            if (version != null && version.nullable) {
                append("if (" + lower + "." + version.getter() + "() == null) {");
                ++indent;
                append(lower + "." + version.setter() + "(existing." + version.getter() + "());");
                --indent;
                append("}");
            }
            --indent;
            append("}");
        }
        evaluatePreconditions(lower);
        nl();
        append(simple + " result = em.merge(" + lower + ");");
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
        append("EntityTag eTag = new EntityTag(Objects.toString(" + entity + "." + version.getter() + "()));");
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
        idPath();
        append("public Response delete" + simple + "(@PathParam(\"id\") " + key.simpleType + " " + key.name
                + requestContext() + ") {");
        ++indent;
        log("delete " + lower + " {}", key.name);
        nl();
        if (primary()) {
            append(simple + " result = em.find(" + simple + ".class, " + key.name + ");");
        } else {
            append(simple + " result = findByKey(" + key.name + ");");
        }
        append("if (result == null) {");
        ++indent;
        append("return Response.status(Status.NOT_FOUND).build();");
        --indent;
        append("}");
        evaluatePreconditions("result");
        nl();
        append("em.remove(result);");
        nl();
        append("return Response.ok(result)" + etag("result") + ".build();");
        --indent;
        append("}");
    }

    private void subresources() {
        for (WebResourceField subresource : WebResourceField.findFields(type, WebSubResource.class.getName())) {
            nl();
            subGET(subresource);
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
        path(plural + "/{id}/" + subresource.name);
        append("public Response get" + simple + subresource.uppercaps() + "(@PathParam(\"id\") " + key.simpleType + " "
                + key.name + requestContext() + ") {");
        ++indent;
        log("get " + subresource.name + " from " + lower + " {}", key.name);
        nl();
        if (primary()) {
            append(simple + " result = em.find(" + simple + ".class, " + key.name + ");");
        } else {
            append(simple + " result = findByKey(" + key.name + ");");
        }
        append("if (result == null) {");
        ++indent;
        append("return Response.status(Status.NOT_FOUND).build();");
        --indent;
        append("}");
        evaluatePreconditions("result");
        nl();
        append("return Response.ok(result." + subresource.getter() + "())" + etag("result") + ".build();");
        --indent;
        append("}");
    }

    private void subPUT(WebResourceField subresource) {
        append("@PUT");
        path(plural + "/{id}/" + subresource.name);
        append("public Response update" + simple + subresource.uppercaps() + "(@PathParam(\"id\") " + key.simpleType
                + " " + key.name + requestContext() + ", " + subresource.simpleType + " " + subresource.name + ") {");
        ++indent;
        log("put " + subresource.name + " {} of " + lower + " {}", subresource.name, key.name);
        nl();
        if (primary()) {
            append(simple + " " + lower + " = em.find(" + simple + ".class, " + key.name + ");");
        } else {
            append(simple + " " + lower + " = findByKey(" + key.name + ");");
        }
        append("if (" + lower + " == null) {");
        ++indent;
        append("return Response.status(Status.NOT_FOUND).build();");
        --indent;
        append("}");
        evaluatePreconditions(lower);
        nl();
        append(lower + "." + subresource.setter() + "(" + subresource.name + ");");
        append("em.flush();");
        nl();
        append("return Response.ok(" + subresource.name + ")" + etag(lower) + ".build();");
        --indent;
        append("}");
    }

    private void subDELETE(WebResourceField subresource) {
        append("@DELETE");
        path(plural + "/{id}/" + subresource.name);
        append("public Response delete" + simple + subresource.uppercaps() + "(@PathParam(\"id\") " + key.simpleType
                + " " + key.name + requestContext() + ") {");
        ++indent;
        log("delete " + subresource.name + " of " + lower + " {}", key.name);
        nl();
        if (primary()) {
            append(simple + " " + lower + " = em.find(" + simple + ".class, " + key.name + ");");
        } else {
            append(simple + " " + lower + " = findByKey(" + key.name + ");");
        }
        append("if (" + lower + " == null) {");
        ++indent;
        append("return Response.status(Status.NOT_FOUND).build();");
        --indent;
        append("}");
        evaluatePreconditions(lower);
        nl();
        append(lower + "." + subresource.setter() + "(null);");
        append("em.flush();");
        nl();
        append("return Response.ok()" + etag(lower) + ".build();");
        --indent;
        append("}");
    }
}
