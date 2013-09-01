package com.github.t1.webresource;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;

import com.github.t1.stereotypes.Annotations;

public abstract class AbstractTraitProvider {

    protected final Class<?> type;
    protected final PojoTraits target;

    public AbstractTraitProvider(Class<?> type, PojoTraits target) {
        this.type = type;
        this.target = target;
    }

    protected boolean typeIs(Class<? extends Annotation> annotationType) {
        return typeAnnotations().isAnnotationPresent(annotationType);
    }

    protected <T extends Annotation> T typeAnnotation(Class<T> annotationType) {
        return typeAnnotations().getAnnotation(annotationType);
    }

    private AnnotatedElement typeAnnotations() {
        return Annotations.on(type);
    }

    public void run() {
        addFields();
        addGetters();
    }

    private void addFields() {
        for (Field field : type.getDeclaredFields()) {
            PojoFieldTrait trait = new PojoFieldTrait(field);
            if (pass(trait)) {
                add(trait);
            }
        }
    }

    protected abstract boolean pass(PojoFieldTrait field);

    protected abstract boolean pass(PojoGetterTrait getter);

    /** esp. for overwriting the name after it was decided to add this trait */
    protected void init(PojoTrait trait) {}

    private void addGetters() {
        for (Method method : type.getDeclaredMethods()) {
            PojoGetterTrait getter = new PojoGetterTrait(method);
            if (pass(getter)) {
                add(getter);
            }
        }
    }

    private void add(PojoTrait getter) {
        init(getter);
        if (!hasTrait(getter.getName())) {
            target.add(getter);
        }
    }

    protected boolean hasTrait(String name) {
        for (Trait trait : target) {
            if (trait.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
