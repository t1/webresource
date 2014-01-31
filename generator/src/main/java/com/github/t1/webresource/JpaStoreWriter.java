package com.github.t1.webresource;

import com.github.t1.webresource.typewriter.IndentedWriter;

/** Writes the JPA specific parts of a WebResouce class */
public class JpaStoreWriter {
    private final IndentedWriter writer;
    private final WebResourceType type;

    public JpaStoreWriter(IndentedWriter writer, WebResourceType type) {
        this.writer = writer;
        this.type = type;
    }

    public void declare() {
        writer.append("@PersistenceContext" + (type.extended ? "(type = PersistenceContextType.EXTENDED)" : ""));
        writer.append("private EntityManager em;");
    }

    public void list() {
        writer.append("CriteriaBuilder builder = em.getCriteriaBuilder();");
        writer.append("CriteriaQuery<" + type.simple + "> query = builder.createQuery(" + type.simple + ".class);");
        writer.append("Root<" + type.simple + "> from = query.from(" + type.simple + ".class);");
        writer.append("Predicate where = null;");
        writer.append("for (String key : queryParams.keySet()) {");
        ++writer.indent;
        writer.append("Predicate predicate = builder.equal(from.get(key), queryParams.getFirst(key));");
        writer.append("if (where == null) {");
        ++writer.indent;
        writer.append("where = predicate;");
        --writer.indent;
        writer.append("} else {");
        ++writer.indent;
        writer.append("where = builder.and(where, predicate);");
        --writer.indent;
        writer.append("}");
        --writer.indent;
        writer.append("}");
        writer.append("if (where != null)");
        ++writer.indent;
        writer.append("query.where(where);");
        --writer.indent;
        writer.append("List<" + type.simple + "> list = em.createQuery(query.select(from)).getResultList();");
    }

    public void find(String variableName) {
        writer.indent();
        writer.out.append(type.simple + " " + variableName + " = ");
        if (type.primary()) {
            writer.out.append("em.find(" + type.simple + ".class, " + type.key.name + ");");
        } else {
            writer.out.append("findByKey(" + type.key.name + ");");
        }
        writer.nl();
    }

    public void findByKey() {
        writer.append("TypedQuery<" + type.simple + "> query = em.createQuery(\"FROM " + type.simple + " WHERE "
                + type.key.name + " = :" + type.key.name + "\", " + type.simple + ".class);");
        writer.append("try {");
        ++writer.indent;
        writer.append("return query.setParameter(\"key\", " + type.key.name + ").getSingleResult();");
        --writer.indent;
        writer.append("} catch (NoResultException e) {");
        ++writer.indent;
        writer.append("return null;");
        --writer.indent;
        writer.append("}");
    }

    public void persist() {
        writer.append("if (" + type.lower + ".getId() == null) {");
        ++writer.indent;
        writer.append("em.persist(" + type.lower + ");");
        --writer.indent;
        writer.append("} else {");
        ++writer.indent;
        writer.append(type.lower + " = em.merge(" + type.lower + ");");
        --writer.indent;
        writer.append("}");
        flush();
    }

    public void merge() {
        writer.append(type.simple + " result = em.merge(" + type.lower + ");");
        flush();
    }

    public void remove() {
        writer.append("em.remove(result);");
    }

    public void flush() {
        writer.append("em.flush();");
    }
}
