package com.github.t1.webresource.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.*;

/**
 * The union of the unique annotations of zero or more {@link AnnotatedElement}s, i.e. if an annotation type is present
 * in an earlier element, the same type annotations in other elements are ignored.
 */
public class UnionAnnotatedElement implements AnnotatedElement {

    private final AnnotatedElement[] elements;

    public UnionAnnotatedElement(AnnotatedElement... elements) {
        this.elements = elements;
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        for (AnnotatedElement element : elements) {
            if (element.isAnnotationPresent(annotationClass)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        for (AnnotatedElement element : elements) {
            T annotation = element.getAnnotation(annotationClass);
            if (annotation != null) {
                return annotation;
            }
        }
        return null;
    }

    @Override
    public Annotation[] getAnnotations() {
        List<Annotation> result = new ArrayList<>();
        for (AnnotatedElement element : elements) {
            for (Annotation annotation : element.getAnnotations()) {
                if (!containsType(result, annotation.getClass())) {
                    result.add(annotation);
                }
            }
        }
        return result.toArray(new Annotation[0]);
    }

    private boolean containsType(List<Annotation> result, Class<? extends Annotation> type) {
        for (Annotation annotation : result) {
            if (type.equals(annotation.getClass())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        List<Annotation> result = new ArrayList<>();
        for (AnnotatedElement element : elements) {
            for (Annotation annotation : element.getDeclaredAnnotations()) {
                if (!containsType(result, annotation.getClass())) {
                    result.add(annotation);
                }
            }
        }
        return result.toArray(new Annotation[0]);
    }
}
