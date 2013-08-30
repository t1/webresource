package com.github.t1.webresource;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;

import com.github.t1.stereotypes.Annotations;

public abstract class PojoPropertiesAbstractStrategy {

    protected final Class<?> type;
    protected final PojoProperties target;

    public PojoPropertiesAbstractStrategy(Class<?> type, PojoProperties target) {
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
            PojoFieldProperty property = new PojoFieldProperty(field);
            if (pass(property)) {
                add(property);
            }
        }
    }

    protected abstract boolean pass(PojoFieldProperty field);

    protected abstract boolean pass(PojoGetterProperty getter);

    /** esp. for overwriting the name after it was decided to add this property */
    protected void init(PojoProperty property) {}

    private void addGetters() {
        for (Method method : type.getDeclaredMethods()) {
            PojoGetterProperty getter = new PojoGetterProperty(method);
            if (pass(getter)) {
                add(getter);
            }
        }
    }

    private void add(PojoProperty getter) {
        init(getter);
        if (!hasProperty(getter.getName())) {
            target.add(getter);
        }
    }

    protected boolean hasProperty(String name) {
        for (Property property : target) {
            if (property.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
