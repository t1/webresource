package com.github.t1.webresource;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

class VersionField extends WebResourceField {
    public static VersionField of(TypeElement type) {
        Element versionField = findVersionField(type);
        if (versionField == null)
            return null;
        return new VersionField(versionField);
    }

    private static Element findVersionField(TypeElement classElement) {
        Element versionField = null;
        for (Element enclosedElement : classElement.getEnclosedElements()) {
            if (ElementKind.FIELD != enclosedElement.getKind())
                continue;
            // the Version type may not be available at compile-time
            if (isAnnotated(enclosedElement, "javax.persistence.Version")) {
                versionField = enclosedElement;
            }
        }
        if (versionField != null)
            return versionField;
        TypeMirror superclass = classElement.getSuperclass();
        if (superclass != null) {
            Element superElement = ((DeclaredType) superclass).asElement();
            return findVersionField((TypeElement) superElement);
        }
        return null;
    }

    private static boolean isAnnotated(Element element, String annotationName) {
        for (AnnotationMirror annotation : element.getAnnotationMirrors()) {
            if (annotationName.equals(annotation.getAnnotationType().toString())) {
                return true;
            }
        }
        return false;
    }

    private final String fullyQualifiedTypeName;
    private final String name;

    private VersionField(Element versionField) {
        this.fullyQualifiedTypeName = versionField.asType().toString();
        this.name = versionField.getSimpleName().toString();
    }

    public boolean nullable() {
        return fullyQualifiedTypeName.contains(".");
    }

    public String packageImport() {
        if (!nullable() || fullyQualifiedTypeName.startsWith("java.lang."))
            return null;
        return fullyQualifiedTypeName;
    }

    public String type() {
        int index = fullyQualifiedTypeName.lastIndexOf('.');
        return index < 0 ? fullyQualifiedTypeName : fullyQualifiedTypeName.substring(index + 1);
    }

    public String name() {
        return name;
    }

    private String uppercaps() {
        if (name == null || name.length() == 0)
            return name;
        if (name.length() == 1)
            return name.toUpperCase();
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    public String getter() {
        return "get" + uppercaps();
    }

    public String setter() {
        return "set" + uppercaps();
    }
}
