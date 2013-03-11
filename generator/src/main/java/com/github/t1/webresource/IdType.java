package com.github.t1.webresource;

import javax.lang.model.element.*;

class IdType {

    final String packageImport;
    final String simpleName;
    final boolean nullable;

    public IdType(TypeElement type) {
        String fullyQualifiedName = fullyQualifiedName(type);
        this.nullable = fullyQualifiedName.contains(".");
        this.packageImport = (nullable) ? packageImport(fullyQualifiedName) : null;
        this.simpleName = simpleName(fullyQualifiedName);
    }

    private String fullyQualifiedName(TypeElement classElement) {
        for (Element enclosedElement : classElement.getEnclosedElements()) {
            if (isIdField(enclosedElement)) {
                return enclosedElement.asType().toString();
            }
        }
        return "long";
    }

    private boolean isIdField(Element element) {
        return ElementKind.FIELD == element.getKind() && isAnnotatedAsId(element);
    }

    private boolean isAnnotatedAsId(Element element) {
        for (AnnotationMirror annotation : element.getAnnotationMirrors()) {
            // the Id type may not be available at compile-time
            if ("javax.persistence.Id".equals(annotation.getAnnotationType().toString())) {
                return true;
            }
        }
        return false;
    }

    private String packageImport(String typeName) {
        if (typeName.startsWith("java.lang."))
            return null;
        return typeName;
    }

    private String simpleName(String typeName) {
        int index = typeName.lastIndexOf('.');
        return index < 0 ? typeName : typeName.substring(index + 1);
    }

    @Override
    public String toString() {
        return simpleName;
    }
}
