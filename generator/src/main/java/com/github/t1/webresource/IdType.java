package com.github.t1.webresource;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

class IdType {

    public static IdType of(TypeElement type) {
        Element idField = fullyQualifiedName(type);
        if (idField == null)
            return null;
        return new IdType(idField);
    }

    private static Element fullyQualifiedName(TypeElement classElement) {
        Element idField = null;
        for (Element enclosedElement : classElement.getEnclosedElements()) {
            if (ElementKind.FIELD != enclosedElement.getKind())
                continue;
            if (isAnnotated(enclosedElement, WebResourceKey.class.getName()))
                return enclosedElement;
            if (isAnnotated(enclosedElement, "javax.persistence.Id")) {
                idField = enclosedElement;
            }
        }
        if (idField != null)
            return idField;
        TypeMirror superclass = classElement.getSuperclass();
        if (superclass != null) {
            Element superElement = ((DeclaredType) superclass).asElement();
            return fullyQualifiedName((TypeElement) superElement);
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

    private final String fullyQualifiedTypeName;
    private final String fieldName;

    private IdType(Element idField) {
        this.fullyQualifiedTypeName = idField.asType().toString();
        this.fieldName = idField.getSimpleName().toString();
    }

    public boolean nullable() {
        return fullyQualifiedTypeName.contains(".");
    }

    public String packageImport() {
        if (!nullable() || fullyQualifiedTypeName.startsWith("java.lang."))
            return null;
        return fullyQualifiedTypeName;
    }

    @Override
    public String toString() {
        int index = fullyQualifiedTypeName.lastIndexOf('.');
        return index < 0 ? fullyQualifiedTypeName : fullyQualifiedTypeName.substring(index + 1);
    }

    public String fieldName() {
        return fieldName;
    }
}
