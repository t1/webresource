package com.github.t1.webresource;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;

import lombok.*;


public abstract class PojoProperty implements Property {

    /**
     * Note that the name of a property does not have to match the name of the field or method... not only that the
     * "get" prefix of a method has to be removed, in JAXB there are annotations to set the name explicitly.
     */
    @Getter
    @Setter
    private String name;

    protected abstract Member member();

    /** is not static and not transient, but public */
    public boolean isPublicMember() {
        int modifiers = member().getModifiers();
        return !Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers) && Modifier.isPublic(modifiers);
    }

    protected final AnnotatedElement annotations;

    public PojoProperty(AnnotatedElement annotations, String name) {
        this.annotations = annotations;
        this.name = name;
    }

    @Override
    public <T extends Annotation> boolean is(Class<T> type) {
        return annotations.isAnnotationPresent(type);
    }

    @Override
    public <T extends Annotation> T get(Class<T> type) {
        return annotations.getAnnotation(type);
    }
}
