package com.github.t1.webresource;

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
    private final IdType idType;
    private final String upperCapsFieldName;

    private int indent = 0;

    public WebResourceWriter(Messager messager, TypeElement type) {
        this.type = type;
        this.pkg = pkg();
        this.simple = type.getSimpleName().toString();
        this.lower = simple.toLowerCase();
        this.plural = plural(lower);
        this.idType = IdType.of(type);
        if (idType == null) {
            messager.printMessage(Kind.ERROR, "can't find @Id field", type);
        }
        this.upperCapsFieldName = (idType == null) ? null : uppercaps(idType.fieldName());
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

    private String uppercaps(String string) {
        if (string == null || string.length() == 0)
            return string;
        if (string.length() == 1)
            return string.toUpperCase();
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }

    public String run() {
        if (idType == null)
            throw new IllegalStateException("no id type found in " + type.getQualifiedName());
        append("package " + pkg + ";");
        nl();
        imports();
        nl();
        clazz();
        return out.toString();
    }

    private void imports() {
        append("import static javax.persistence.PersistenceContextType.*;");
        nl();
        if (idType.packageImport() != null)
            append("import " + idType.packageImport() + ";");
        append("import java.util.List;");
        nl();
        append("import javax.ejb.Stateless;");
        append("import javax.persistence.*;");
        nl();
        append("import javax.ws.rs.*;");
        append("import javax.ws.rs.core.*;");
        append("import javax.ws.rs.core.Response.Status;");
    }

    private void clazz() {
        path("/");
        append("@Stateless");
        append("public class " + simple + "WebResource {");
        ++indent;
        entityManager();
        nl();
        GET_ALL();
        nl();
        GET_ONE();
        nl();
        POST();
        nl();
        PUT();
        nl();
        DELETE();
        --indent;
        append("}");
    }

    private void path(String path) {
        append("@Path(\"" + path + "\")");
    }

    private void entityManager() {
        append("@PersistenceContext(type=EXTENDED)");
        append("private EntityManager em;");
    }

    private void GET_ALL() {
        append("@GET");
        path(plural);
        append("public List<" + simple + "> getAll() {");
        ++indent;
        append("return em.createQuery(\"FROM " + simple + " ORDER BY id\", " + simple + ".class).getResultList();");
        --indent;
        append("}");
    }

    private void GET_ONE() {
        append("@GET");
        idPath();
        append("public Response get" + simple + "(@PathParam(\"id\") " + idType + " id) {");
        ++indent;
        append(simple + " result = em.find(" + simple + ".class, id);");
        append("if (result == null) {");
        ++indent;
        append("return Response.status(Status.NOT_FOUND).build();");
        --indent;
        append("}");
        append("return Response.ok(result).build();");
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
        append("em.persist(" + lower + ");");
        append("em.flush();");
        nl();
        append("UriBuilder builder = uriInfo.getBaseUriBuilder();");
        append("builder.path(\"" + plural + "\").path(\"\" + " + lower + "." + getter() + "());");
        append("return Response.created(builder.build()).build();");
        --indent;
        append("}");
    }

    private String getter() {
        return "get" + upperCapsFieldName;
    }

    private String setter() {
        return "set" + upperCapsFieldName;
    }

    private void PUT() {
        append("@PUT");
        idPath();
        append("public Response update" + simple + "(@PathParam(\"id\") " + idType + " id, " + simple + " " + lower
                + ") {");
        ++indent;
        if (idType.nullable()) {
            append("if (" + lower + "." + getter() + "() == null) {");
            ++indent;
            append(lower + "." + setter() + "(id);");
            --indent;
            appendIndent();
            out.append("} else ");
        } else {
            appendIndent();
        }
        out.append("if (id != " + lower + "." + getter() + "()) {");
        nl();
        ++indent;
        append("String message = \"id conflict! path=\" + id + \", body=\" + " + lower + "." + getter()
                + "() + \".\\n\"");
        append("    + \"either leave the id in the body null or set it to the same id\";");
        append("return Response.status(Status.BAD_REQUEST).entity(message).build();");
        --indent;
        append("}");
        append(simple + " result = em.merge(" + lower + ");");
        append("if (result == null) {");
        ++indent;
        append("return Response.status(Status.NOT_FOUND).build();");
        --indent;
        append("}");
        append("return Response.ok(result).build();");
        --indent;
        append("}");
    }

    private void DELETE() {
        append("@DELETE");
        idPath();
        append("public Response delete" + simple + "(@PathParam(\"id\") " + idType + " id) {");
        ++indent;
        append(simple + " result = em.find(" + simple + ".class, id);");
        append("if (result == null) {");
        ++indent;
        append("return Response.status(Status.NOT_FOUND).build();");
        --indent;
        append("}");
        append("em.remove(result);");
        append("return Response.ok(result).build();");
        --indent;
        append("}");
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
}
