package com.github.t1.webresource.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import com.github.t1.stereotypes.Annotations;

public class PojoFieldTrait extends PojoTrait {

    private final Field field;

    public PojoFieldTrait(Field field) {
        super(Annotations.on(field), field.getName());
        this.field = field;
    }

    @Override
    protected Field member() {
        return field;
    }

    @Override
    public Object of(Object object) {
        try {
            field.setAccessible(true);
            return field.get(object);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("can't get " + getName() + " of " + object, e);
        }
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("field ").append(getName());
        out.append(" of ").append(field.getDeclaringClass().getName());
        if (annotations.getAnnotations().length > 0) {
            out.append(": ");
            for (Annotation annotation : annotations.getAnnotations()) {
                out.append(annotation).append(" ");
            }
        }
        return out.toString();
    }
}
