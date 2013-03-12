package com.github.t1.webresource;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

class IdType {

    public static IdType of(TypeElement type) {
        String fullyQualifiedName = fullyQualifiedName(type);
        if (fullyQualifiedName == null)
            return null;
        return new IdType(fullyQualifiedName);
    }

    private static String fullyQualifiedName(TypeElement classElement) {
        for (Element enclosedElement : classElement.getEnclosedElements()) {
            if (isIdField(enclosedElement)) {
                return enclosedElement.asType().toString();
            }
        }
        TypeMirror superclass = classElement.getSuperclass();
        if (superclass != null) {
            Element superElement = ((DeclaredType) superclass).asElement();
            return fullyQualifiedName((TypeElement) superElement);
        }
        return null;
    }

    private static boolean isIdField(Element element) {
        return ElementKind.FIELD == element.getKind() && isAnnotatedAsId(element);
    }

    private static boolean isAnnotatedAsId(Element element) {
        for (AnnotationMirror annotation : element.getAnnotationMirrors()) {
            // the Id type may not be available at compile-time
            if ("javax.persistence.Id".equals(annotation.getAnnotationType().toString())) {
                return true;
            }
        }
        return false;
    }

    private final String fullyQualifiedName;

    private IdType(String fullyQualifiedName) {
        this.fullyQualifiedName = fullyQualifiedName;
    }

    public boolean nullable() {
        return fullyQualifiedName.contains(".");
    }

    public String packageImport() {
        if (!nullable() || fullyQualifiedName.startsWith("java.lang."))
            return null;
        return fullyQualifiedName;
    }

    @Override
    public String toString() {
        int index = fullyQualifiedName.lastIndexOf('.');
        return index < 0 ? fullyQualifiedName : fullyQualifiedName.substring(index + 1);
    }
}
