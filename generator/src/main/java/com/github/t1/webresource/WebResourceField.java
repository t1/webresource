package com.github.t1.webresource;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import com.github.t1.webresource.typewriter.TypeString;

public class WebResourceField {
    protected static WebResourceField findField(TypeElement classElement, String annotationTypeName) {
        List<WebResourceField> list = findFields(classElement, annotationTypeName);
        if (list.isEmpty())
            return null;
        if (list.size() == 1)
            return list.get(0);
        throw new IllegalArgumentException("more than one " + annotationTypeName + " field found: " + list);
    }

    static List<WebResourceField> findFields(TypeElement classElement, String annotationTypeName) {
        List<WebResourceField> result = new ArrayList<>();
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

    static boolean isAnnotated(Element element, String annotationName) {
        return getAnnotation(element, annotationName) != null;
    }

    static AnnotationMirror getAnnotation(Element element, String annotationName) {
        for (AnnotationMirror annotation : element.getAnnotationMirrors()) {
            if (annotationName.equals(annotation.getAnnotationType().toString())) {
                return annotation;
            }
        }
        return null;
    }

    private final Element field;

    /** The field name */
    final String name;
    /** What imports are required for this type */
    public final List<String> imports = new ArrayList<>();

    final TypeString type;

    private WebResourceField(Element field) {
        this.field = field;
        this.name = field.getSimpleName().toString();
        this.type = new TypeString(field.asType().toString());
        this.imports.addAll(type.imports);
    }

    @Override
    public int hashCode() {
        return field.hashCode();
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
        return field.equals(other.field);
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
}
