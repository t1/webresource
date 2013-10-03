package com.github.t1.webresource;

import java.util.List;

import javax.lang.model.element.*;

/** TODO switch this and WebResourceField to meta, move to commons, and join with WebResourceTypeInfo */
class WebResourceType {
    final TypeElement type;
    final String pkg;
    final String simple;
    final String lower;
    final String plural;
    final String qualified;
    final boolean extended;

    final String entityName;

    public WebResourceType(TypeElement type) {
        this.type = type;
        this.pkg = pkg();
        this.simple = type.getSimpleName().toString();
        this.entityName = entity();
        this.lower = simple.toLowerCase();
        this.plural = new WebResourceTypeInfo(simple).plural;
        this.qualified = qualified();
        this.extended = isExtended();
    }

    private String pkg() {
        for (Element element = type; element != null; element = element.getEnclosingElement()) {
            if (ElementKind.PACKAGE == element.getKind()) {
                return ((PackageElement) element).getQualifiedName().toString();
            }
        }
        throw new IllegalStateException("no package for " + type);
    }

    private boolean isExtended() {
        WebResource annotation = type.getAnnotation(WebResource.class);
        if (annotation == null)
            throw new RuntimeException("expected type to be annotated as WebResource: " + type);
        return annotation.extended();
    }

    private String qualified() {
        return type.getQualifiedName().toString();
    }

    private String entity() {
        AnnotationMirror annotation = WebResourceField.getAnnotation(type, "javax.persistence.Entity");
        if (annotation != null) {
            AnnotationValue name = annotation.getElementValues().get("name");
            if (name != null) {
                return name.toString();
            }
        }
        return type.getSimpleName().toString();
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
