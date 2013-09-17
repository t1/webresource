package com.github.t1.webresource.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;

import lombok.*;
import lombok.experimental.Accessors;


public abstract class PojoTrait extends AbstractTrait {

    /**
     * Note that the name of a trait does not have to match the name of the field or method... not only that the "get"
     * prefix of a method has to be removed, in JAXB there are annotations to set the name explicitly.
     */
    @Getter
    @Setter
    @Accessors(fluent = true)
    private String name;

    protected abstract Member member();

    /** is not static and not transient, but public */
    public boolean isPublicMember() {
        int modifiers = member().getModifiers();
        return !Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers) && Modifier.isPublic(modifiers);
    }

    protected final AnnotatedElement annotations;

    public PojoTrait(AnnotatedElement annotations, String name) {
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
