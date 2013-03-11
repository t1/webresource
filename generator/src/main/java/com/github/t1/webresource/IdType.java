package com.github.t1.webresource;

import javax.lang.model.element.*;

class IdType {

    final String packageImport;
    final String name;
    final boolean nullable;

    public IdType(TypeElement type) {
        String idType = idType(type);
        this.packageImport = pkg(idType);
        this.name = typeName(idType);
        this.nullable = idType.contains(".");
    }

    private String idType(TypeElement classElement) {
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

    private String pkg(String typeName) {
        int index = typeName.lastIndexOf('.');
        if (index < 0)
            return null;
        String pkg = typeName.substring(0, index);
        if ("java.lang".equals(pkg))
            return null;
        return pkg;
    }

    private String typeName(String typeName) {
        int index = typeName.lastIndexOf('.');
        return index < 0 ? typeName : typeName.substring(index + 1);
    }
}
