package com.github.t1.webresource;

import javax.persistence.*;

import com.github.t1.webresource.typewriter.*;

/** Writes the JPA specific parts of a WebResouce class */
public class JpaStoreWriter {
    private final IndentedWriter writer;
    private final WebResourceType type;
    private final ClassBuilder typeWriter;

    public JpaStoreWriter(IndentedWriter writer, WebResourceType type, ClassBuilder typeWriter) {
        this.writer = writer;
        this.type = type;
        this.typeWriter = typeWriter;
    }

    public void declare() {
        FieldBuilder field = typeWriter.field(EntityManager.class, "em");
        AnnotationBuilder annotation = field.annotate(PersistenceContext.class);
        if (type.extended) {
            annotation.type(PersistenceContextType.EXTENDED);
        }
    }

    public void list() {
        writer.println("CriteriaBuilder builder = em.getCriteriaBuilder();");
        writer.println("CriteriaQuery<" + type.simple + "> query = builder.createQuery(" + type.simple + ".class);");
        writer.println("Root<" + type.simple + "> from = query.from(" + type.simple + ".class);");
        writer.println("Predicate where = null;");
        writer.println("for (String key : queryParams.keySet()) {");
        ++writer.indent;
        writer.println("Predicate predicate = builder.equal(from.get(key), queryParams.getFirst(key));");
        writer.println("if (where == null) {");
        ++writer.indent;
        writer.println("where = predicate;");
        --writer.indent;
        writer.println("} else {");
        ++writer.indent;
        writer.println("where = builder.and(where, predicate);");
        --writer.indent;
        writer.println("}");
        --writer.indent;
        writer.println("}");
        writer.println("if (where != null)");
        ++writer.indent;
        writer.println("query.where(where);");
        --writer.indent;
        writer.println("List<" + type.simple + "> list = em.createQuery(query.select(from)).getResultList();");
    }

    public void find(String variableName) {
        writer.indent();
        writer.out.append(type.simple + " " + variableName + " = ");
        if (type.primary()) {
            writer.out.append("em.find(" + type.simple + ".class, " + type.key.name + ");");
        } else {
            writer.out.append("findByKey(" + type.key.name + ");");
        }
        writer.println();
    }

    public void findByKey() {
        writer.println("TypedQuery<" + type.simple + "> query = em.createQuery(\"FROM " + type.simple + " WHERE "
                + type.key.name + " = :" + type.key.name + "\", " + type.simple + ".class);");
        writer.println("try {");
        ++writer.indent;
        writer.println("return query.setParameter(\"key\", " + type.key.name + ").getSingleResult();");
        --writer.indent;
        writer.println("} catch (NoResultException e) {");
        ++writer.indent;
        writer.println("return null;");
        --writer.indent;
        writer.println("}");
    }

    public void persist() {
        writer.println("if (" + type.lower + ".getId() == null) {");
        ++writer.indent;
        writer.println("em.persist(" + type.lower + ");");
        --writer.indent;
        writer.println("} else {");
        ++writer.indent;
        writer.println(type.lower + " = em.merge(" + type.lower + ");");
        --writer.indent;
        writer.println("}");
        flush();
    }

    public void merge() {
        writer.println(type.simple + " result = em.merge(" + type.lower + ");");
        flush();
    }

    public void remove() {
        writer.println("em.remove(result);");
    }

    public void flush() {
        writer.println("em.flush();");
    }
}
