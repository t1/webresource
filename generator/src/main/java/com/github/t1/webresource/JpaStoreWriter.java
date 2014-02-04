package com.github.t1.webresource;

import java.io.PrintWriter;

import javax.persistence.*;

import com.github.t1.webresource.typewriter.*;

/** Writes the JPA specific parts of a WebResouce class */
public class JpaStoreWriter {
    private final WebResourceType type;

    public JpaStoreWriter(WebResourceType type) {
        this.type = type;
    }

    public void declare(ClassBuilder typeWriter) {
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

    public void find(PrintWriter body, String variableName) {
        body.append(type.simple + " " + variableName + " = ");
        if (type.primary()) {
            body.append("em.find(" + type.simple + ".class, " + type.key.name + ");");
        } else {
            body.append("findByKey(" + type.key.name + ");");
        }
        body.println();
    }

    public void findByKey(PrintWriter body) {
        body.println("TypedQuery<" + type.simple + "> query = em.createQuery(\"FROM " + type.simple + " WHERE "
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
