package com.github.t1.webresource.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.google.common.base.Predicate;


public abstract class PojoTrait extends ObjectTrait {
    /**
     * Note that the name of a trait does not have to match the name of the field or method... not only that the "get"
     * prefix of a method has to be removed, e.g. in JAXB, there are annotations to set the name explicitly.
     */
    @Getter
    @Accessors(fluent = true)
    private final String name;

    protected abstract Member member();

    protected AnnotatedElement annotations;
    private final Predicate<PojoTrait> visible;

    public PojoTrait(AnnotatedElement annotations, String name, Predicate<PojoTrait> visible) {
        this.annotations = annotations;
        this.name = name;
        this.visible = visible;
    }

    @Override
    public boolean visible() {
        return visible.apply(this);
    }

    @Override
    public <T extends Annotation> boolean is(Class<T> type) {
        return annotations.isAnnotationPresent(type);
    }

    @Override
    public <T extends Annotation> T get(Class<T> type) {
        return annotations.getAnnotation(type);
    }

    public void add(AnnotatedElement newAnnotations) {
        if (isEmpty(newAnnotations))
            return;
        if (isEmpty(annotations)) {
            annotations = newAnnotations;
        } else {
            annotations = new UnionAnnotatedElement(annotations, newAnnotations);
        }
    }

    private boolean isEmpty(AnnotatedElement newAnnotations) {
        return newAnnotations.getAnnotations().length == 0;
    }
}
