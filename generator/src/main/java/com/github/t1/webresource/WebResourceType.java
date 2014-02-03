package com.github.t1.webresource;

import java.util.List;

import javax.lang.model.element.*;

public class WebResourceType {

    public static Class<?> type(String typeName) {
        switch (typeName) {
        case "long":
            return Long.TYPE;
        case "int":
            return Integer.TYPE;
        case "short":
            return Short.TYPE;
        case "byte":
            return Byte.TYPE;
        case "char":
            return Character.TYPE;
        case "double":
            return Double.TYPE;
        case "float":
            return Float.TYPE;
        case "boolean":
            return Boolean.TYPE;
        case "void":
            return Void.TYPE;
        default:
            return forName(typeName);
        }
    }

    private static Class<?> forName(String typeName) {
        try {
            return Class.forName(typeName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    final TypeElement type;
    final String pkg;
    final String simple;
    final String entityName;
    final String lower;
    final String plural;
    final String qualified;
    final boolean extended;
    public final WebResourceField id;
    public final WebResourceField key;
    public final WebResourceField version;
    final List<WebResourceField> subResourceFields;

    public WebResourceType(TypeElement type) {
        this.type = type;
        this.pkg = pkg();
        this.simple = type.getSimpleName().toString();
        this.entityName = entity();
        this.lower = simple.toLowerCase();
        this.plural = new WebResourceTypeInfo(simple).plural;
        this.qualified = qualified();
        this.extended = isExtended();
        this.id = id();
        this.key = key();
        this.version = version();
        this.subResourceFields = subResourceFields();
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

    private WebResourceField id() {
        // don't use the Id type itself, it may not be available at compile-time
        return WebResourceField.findField(type, "javax.persistence.Id");
    }

    private WebResourceField key() {
        WebResourceField keyField = WebResourceField.findField(type, WebResourceKey.class.getName());
        return (keyField == null) ? id : keyField;
    }

    private WebResourceField version() {
        // don't use the Version type itself, it may not be available at compile-time
        return WebResourceField.findField(type, "javax.persistence.Version");
    }

    private List<WebResourceField> subResourceFields() {
        return WebResourceField.findFields(type, WebSubResource.class.getName());
    }

    public boolean primary() {
        return id.equals(key);
    }

    public Class<?> type() {
        return type(qualified);
    }
}
