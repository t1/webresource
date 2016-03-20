package com.github.t1.webresource;

import com.github.t1.webresource.typewriter.*;

import javax.persistence.*;
import java.io.PrintWriter;

/** Writes the JPA specific parts of a WebResource class */
class JpaStoreWriter {
    private final WebResourceType type;

    JpaStoreWriter(WebResourceType type) {
        this.type = type;
    }

    void declare(ClassBuilder typeWriter) {
        FieldBuilder field = typeWriter.field(EntityManager.class, "em");
        AnnotationBuilder annotation = field.annotate(PersistenceContext.class);
        if (type.extended) {
            annotation.type(PersistenceContextType.EXTENDED);
        }
    }

    public void list(PrintWriter body) {
        body.println("CriteriaBuilder builder = em.getCriteriaBuilder();");
        body.println("CriteriaQuery<" + type.simple + "> query = builder.createQuery(" + type.simple + ".class);");
        body.println("Root<" + type.simple + "> from = query.from(" + type.simple + ".class);");
        body.println("Predicate where = null;");
        body.println("for (String key : queryParams.keySet()) {");
        body.println("    Predicate predicate = builder.equal(from.get(key), queryParams.getFirst(key));");
        body.println("    if (where == null) {");
        body.println("        where = predicate;");
        body.println("    } else {");
        body.println("        where = builder.and(where, predicate);");
        body.println("    }");
        body.println("}");
        body.println("if (where != null)");
        body.println("    query.where(where);");
        body.println("List<" + type.simple + "> list = em.createQuery(query.select(from)).getResultList();");
    }

    void find(PrintWriter body, String variableName) {
        body.append(type.simple).append(" ").append(variableName).append(" = ");
        if (type.primary()) {
            body.append("em.find(").append(type.simple).append(".class, ").append(type.key.name).append(");");
        } else {
            body.append("findByKey(").append(type.key.name).append(");");
        }
        body.println();
    }

    void findByKey(PrintWriter body) {
        body.println("TypedQuery<" + type.simple + "> query = em.createQuery(\"FROM " + type.entityName + " WHERE "
                + type.key.name + " = :" + type.key.name + "\", " + type.simple + ".class);");
        body.println("try {");
        body.println("    return query.setParameter(\"key\", " + type.key.name + ").getSingleResult();");
        body.println("} catch (NoResultException e) {");
        body.println("    return null;");
        body.println("}");
    }

    public void persist(PrintWriter body) {
        body.println("if (" + type.lower + ".getId() == null) {");
        body.println("    em.persist(" + type.lower + ");");
        body.println("} else {");
        body.println("    " + type.lower + " = em.merge(" + type.lower + ");");
        body.println("}");
        flush(body);
    }

    public void merge(PrintWriter body) {
        body.println(type.simple + " result = em.merge(" + type.lower + ");");
        flush(body);
    }

    public void remove(PrintWriter body) {
        body.println("em.remove(result);");
    }

    public void flush(PrintWriter body) {
        body.println("em.flush();");
    }
}
