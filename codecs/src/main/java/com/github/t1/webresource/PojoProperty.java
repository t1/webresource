package com.github.t1.webresource;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;


public abstract class PojoProperty implements Property {

    protected abstract Member member();

    /** is not static and not transient, but public */
    public boolean isPublicMember() {
        int modifiers = member().getModifiers();
        return !Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers) && Modifier.isPublic(modifiers);
    }

    protected final AnnotatedElement annotations;

    public PojoProperty(AnnotatedElement annotations) {
        this.annotations = annotations;
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
