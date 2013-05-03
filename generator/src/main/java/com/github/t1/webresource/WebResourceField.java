package com.github.t1.webresource;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

class WebResourceField {
    protected static WebResourceField findField(TypeElement classElement, String annotationTypeName) {
        List<WebResourceField> list = findFields(classElement, annotationTypeName);
        if (list.isEmpty())
            return null;
        if (list.size() == 1)
            return list.get(0);
        throw new IllegalArgumentException("more than one " + annotationTypeName + " field found: " + list);
    }

    protected static List<WebResourceField> findFields(TypeElement classElement, String annotationTypeName) {
        List<WebResourceField> result = new ArrayList<WebResourceField>();
        for (Element enclosedElement : classElement.getEnclosedElements()) {
            if (ElementKind.FIELD != enclosedElement.getKind())
                continue;
            if (isAnnotated(enclosedElement, annotationTypeName)) {
                result.add(new WebResourceField(enclosedElement));
            }
        }
        TypeMirror superclass = classElement.getSuperclass();
        if (superclass != null && superclass instanceof DeclaredType) {
            try {
                Element superElement = ((DeclaredType) superclass).asElement();
                result.addAll(findFields((TypeElement) superElement, annotationTypeName));
            } catch (RuntimeException e) {
                throw new RuntimeException("findField in superclass of " + classElement.getQualifiedName(), e);
            }
        }
        return result;
    }

    private static boolean isAnnotated(Element element, String annotationName) {
        for (AnnotationMirror annotation : element.getAnnotationMirrors()) {
            if (annotationName.equals(annotation.getAnnotationType().toString())) {
                return true;
            }
        }
        return false;
    }

    protected final String fullyQualifiedTypeName;
    protected final String name;

    private WebResourceField(Element field) {
        this.fullyQualifiedTypeName = field.asType().toString();
        this.name = field.getSimpleName().toString();
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

    public String uppercaps() {
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fullyQualifiedTypeName == null) ? 0 : fullyQualifiedTypeName.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WebResourceField other = (WebResourceField) obj;
        if (fullyQualifiedTypeName == null) {
            if (other.fullyQualifiedTypeName != null)
                return false;
        } else if (!fullyQualifiedTypeName.equals(other.fullyQualifiedTypeName))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
