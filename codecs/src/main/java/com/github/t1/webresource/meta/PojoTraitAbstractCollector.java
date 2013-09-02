package com.github.t1.webresource.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;

/**
 * TODO traits of super classes
 */
public abstract class PojoTraitAbstractCollector {

    private final Class<?> type;
    private final PojoTraits target;
    private final AnnotatedElement annotations;

    public PojoTraitAbstractCollector(Class<?> type, PojoTraits target, AnnotatedElement annotations) {
        this.type = type;
        this.target = target;
        this.annotations = annotations;
    }

    protected boolean typeIs(Class<? extends Annotation> annotationType) {
        return annotations.isAnnotationPresent(annotationType);
    }

    protected <T extends Annotation> T typeAnnotation(Class<T> annotationType) {
        return annotations.getAnnotation(annotationType);
    }

    public void run() {
        addFields();
        addGetters();
        sort(target);
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

    protected void sort(PojoTraits target) {}
}
