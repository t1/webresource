package com.github.t1.webresource;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    final Element field;

    /** The field name */
    final String name;
    /** Is this a non-primitive type, i.e. can it be null */
    final boolean nullable;
    /** What imports are required for this type */
    final List<String> imports = new ArrayList<String>();
    /** The unqualified type name */
    final String simpleType;
    /** Is this a collection type, i.e. List, Set, etc. */
    final boolean isCollection;
    /** The unqualified type of the elements in the collection or the same as {@link #simpleType} */
    final String uncollectedType;

    private WebResourceField(Element field) {
        this.field = field;
        this.name = field.getSimpleName().toString();
        TypeString typeString = new TypeString(field.asType().toString());
        this.nullable = typeString.nullable;
        this.imports.addAll(typeString.imports);
        this.simpleType = typeString.simpleType;
        this.isCollection = isCollection();
        this.uncollectedType = uncollected();
    }

    private static final Pattern COLLECTION = Pattern.compile("(List|Set|Collection)<(.*)>");

    private boolean isCollection() {
        return COLLECTION.matcher(simpleType).matches();
    }

    private String uncollected() {
        Matcher matcher = COLLECTION.matcher(simpleType);
        if (!matcher.matches())
            return simpleType;
        return matcher.group(2);
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
