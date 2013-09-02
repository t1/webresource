package com.github.t1.webresource.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/** An {@link AnnotatedElement} for nothing. */
public final class NullAnnotatedElement implements AnnotatedElement {
    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return false;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return null;
    }

    @Override
    public Annotation[] getAnnotations() {
        return new Annotation[0];
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        return new Annotation[0];
    }
}