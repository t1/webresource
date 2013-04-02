package com.github.t1.webresource;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

class KeyField extends WebResourceField {
    public static KeyField of(TypeElement type) {
        Element idField = findIdField(type);
        if (idField == null)
            return null;
        boolean primary = isPrimary(idField);
        return new KeyField(idField, primary);
    }

    private static Element findIdField(TypeElement classElement) {
        Element idField = null;
        for (Element enclosedElement : classElement.getEnclosedElements()) {
            if (ElementKind.FIELD != enclosedElement.getKind())
                continue;
            if (isAnnotated(enclosedElement, WebResourceKey.class.getName()))
                return enclosedElement;
            // the Id type may not be available at compile-time
            if (isPrimary(enclosedElement)) {
                idField = enclosedElement;
            }
        }
        if (idField != null)
            return idField;
        TypeMirror superclass = classElement.getSuperclass();
        if (superclass != null) {
            Element superElement = ((DeclaredType) superclass).asElement();
            return findIdField((TypeElement) superElement);
        }
        return null;
    }

    private static boolean isAnnotated(Element element, String annotationName) {
        for (AnnotationMirror annotation : element.getAnnotationMirrors()) {
            // the Id type may not be available at compile-time
            if (annotationName.equals(annotation.getAnnotationType().toString())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isPrimary(Element enclosedElement) {
        return isAnnotated(enclosedElement, "javax.persistence.Id");
    }

    private final String fullyQualifiedTypeName;
    private final String name;
    private final boolean primary;

    private KeyField(Element idField, boolean primary) {
        this.primary = primary;
        this.fullyQualifiedTypeName = idField.asType().toString();
        this.name = idField.getSimpleName().toString();
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

    public boolean primary() {
        return primary;
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
