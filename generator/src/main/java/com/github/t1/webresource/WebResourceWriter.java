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
        this.key = keyField();
        if (id == null)
            messager.printMessage(Kind.ERROR, "can't find @Id or @WebResourceKey field", type);
        this.version = getVersionField(type);
        this.extended = isExtended(type);
    }

    private WebResourceField getIdField(TypeElement type) {
        // don't use the Id type itself, it may not be available at compile-time
        Element idField = WebResourceField.findField(type, "javax.persistence.Id");
        if (idField == null)
            return null;
        return new WebResourceField(idField);
    }

    private WebResourceField keyField() {
        WebResourceField key = getKeyField();
        if (key != null)
            return key;
        return id;
    }

    private WebResourceField getKeyField() {
        Element idField = WebResourceField.findField(type, WebResourceKey.class.getName());
        if (idField == null)
            return null;
        return new WebResourceField(idField);
    }

    private WebResourceField getVersionField(TypeElement type) {
        // don't use the Version type itself, it may not be available at compile-time
        Element versionField = WebResourceField.findField(type, "javax.persistence.Version");
        if (versionField == null)
            return null;
        return new WebResourceField(versionField);
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
        importField(id);
        importField(key);
        append("import java.util.List;");
        nl();
        append("import javax.ejb.Stateless;");
        append("import javax.persistence.*;");
        nl();
        append("import javax.ws.rs.*;");
        append("import javax.ws.rs.core.*;");
        append("import javax.ws.rs.core.Response.Status;");
        nl();
        append("import org.slf4j.*;");
    }

    private void importField(WebResourceField field) {
        if (field != null && field.packageImport() != null) {
            append("import " + field.packageImport() + ";");
        }
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
        list();
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

    private void list() {
        append("@GET");
        path(plural);
        append("public List<" + simple + "> list() {");
        ++indent;
        log("getAll");
        nl();
        append("return em.createQuery(\"FROM " + simple + " ORDER BY " + key.name() + "\", " + simple
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
        append("public Response get" + simple + "(@PathParam(\"id\") " + key.type() + " " + key.name() + ") {");
        ++indent;
        log("get {}", key.name());
        nl();
        if (primary()) {
            findByPrimaryKey();
        } else {
            findBySecondaryKey();
        }
        append("if (result == null) {");
        ++indent;
        append("return Response.status(Status.NOT_FOUND).build();");
        --indent;
        append("}");
        append("return Response.ok(result).build();");
        --indent;
        append("}");
    }

    private void findByPrimaryKey() {
        append(simple + " result = em.find(" + simple + ".class, " + key.name() + ");");
    }

    private void findBySecondaryKey() {
        append(simple + " result = findByKey(" + key.name() + ");");
    }

    private void findByKeyMethod() {
        append("private " + simple + " findByKey(" + key.type() + " " + key.name() + ") {");
        ++indent;
        append("TypedQuery<" + simple + "> query = em.createQuery(\"FROM " + simple + " WHERE " + key.name() + " = :"
                + key.name() + "\", " + simple + ".class);");
        append("try {");
        ++indent;
        append("return query.setParameter(\"key\", " + key.name() + ").getSingleResult();");
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
        log("post {}", lower);
        nl();
        append("em.persist(" + lower + ");");
        append("em.flush();");
        nl();
        append("UriBuilder builder = uriInfo.getBaseUriBuilder();");
        append("builder.path(\"" + plural + "\").path(\"\" + " + lower + "." + key.getter() + "());");
        append("return Response.created(builder.build()).build();");
        --indent;
        append("}");
    }

    private void PUT() {
        append("@PUT");
        idPath();
        append("public Response update" + simple + "(@PathParam(\"id\") " + key.type() + " " + key.name() + ", "
                + simple + " " + lower + ") {");
        ++indent;
        log("put " + key.name() + " {}: {}", key.name(), lower);
        nl();
        if (key.nullable()) {
            append("if (" + lower + "." + key.getter() + "() == null) {");
            ++indent;
            append(lower + "." + key.setter() + "(" + key.name() + ");");
            --indent;
            append("} else if (!" + lower + "." + key.getter() + "().equals(" + key.name() + ")) {");
        } else {
            append("if (" + key.name() + " != " + lower + "." + key.getter() + "()) {");
        }
        ++indent;
        append("String message = \"" + key.name() + " conflict! path=\" + " + key.name() + " + \", body=\" + " + lower
                + "." + key.getter() + "() + \".\\n\"");
        append("    + \"either leave the " + key.name() + " in the body null or set it to the same " + key.name()
                + "\";");
        append("return Response.status(Status.BAD_REQUEST).entity(message).build();");
        --indent;
        append("}");
        if (!primary()) {
            append("if (" + lower + "." + id.getter() + "() == null) {");
            ++indent;
            append(simple + " existing = findByKey(" + key.name() + ");");
            append("if (existing == null) {");
            ++indent;
            append("return Response.status(Status.NOT_FOUND).build();");
            --indent;
            append("}");
            append(lower + "." + id.setter() + "(existing." + id.getter() + "());");
            if (version != null && version.nullable()) {
                append("if (" + lower + "." + version.getter() + "() == null) {");
                ++indent;
                append(lower + "." + version.setter() + "(existing." + version.getter() + "());");
                --indent;
                append("}");
            }
            --indent;
            append("}");
        }
        append(simple + " result = em.merge(" + lower + ");");
        append("if (result == null) {");
        ++indent;
        if (primary()) {
            append("return Response.status(Status.NOT_FOUND).build();");
        } else {
            append("throw new IllegalStateException(\"expected to be able to merge " + key.name() + " \" + "
                    + key.name() + ");");
        }
        --indent;
        append("}");
        append("return Response.ok(result).build();");
        --indent;
        append("}");
    }

    private void DELETE() {
        append("@DELETE");
        idPath();
        append("public Response delete" + simple + "(@PathParam(\"id\") " + key.type() + " " + key.name() + ") {");
        ++indent;
        log("delete {}", key.name());
        nl();
        if (primary()) {
            append(simple + " result = em.find(" + simple + ".class, " + key.name() + ");");
        } else {
            append(simple + " result = findByKey(" + key.name() + ");");
        }
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
