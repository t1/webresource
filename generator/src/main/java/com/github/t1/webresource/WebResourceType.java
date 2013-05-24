package com.github.t1.webresource;

import java.util.List;

import javax.lang.model.element.*;
import javax.persistence.Entity;

class WebResourceType {
    final TypeElement type;
    final String pkg;
    final String simple;
    final String entityName;
    final String lower;
    final String plural;
    final boolean extended;

    public WebResourceType(TypeElement type) {
        this.type = type;
        this.pkg = pkg();
        this.simple = type.getSimpleName().toString();
        this.entityName = entity(type);
        this.lower = simple.toLowerCase();
        this.plural = plural(lower);
        this.extended = isExtended(type);
    }

    private String pkg() {
        for (Element element = type; element != null; element = element.getEnclosingElement()) {
            if (ElementKind.PACKAGE == element.getKind()) {
                return ((PackageElement) element).getQualifiedName().toString();
            }
        }
        throw new IllegalStateException("no package for " + type);
    }

    private String entity(TypeElement type) {
        Entity entity = type.getAnnotation(Entity.class);
        if (entity == null)
            return type.getSimpleName().toString();
        return entity.name();
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

    public String getQualifiedName() {
        return type.getQualifiedName().toString();
    }

    public WebResourceField getIdField() {
        // don't use the Id type itself, it may not be available at compile-time
        return WebResourceField.findField(type, "javax.persistence.Id");
    }

    public WebResourceField getKeyField() {
        WebResourceField keyField = WebResourceField.findField(type, WebResourceKey.class.getName());
        return (keyField == null) ? getIdField() : keyField;
    }

    public WebResourceField getVersionField() {
        // don't use the Version type itself, it may not be available at compile-time
        return WebResourceField.findField(type, "javax.persistence.Version");
    }

    public List<WebResourceField> getSubResourceFields() {
        return WebResourceField.findFields(type, WebSubResource.class.getName());
    }
}
