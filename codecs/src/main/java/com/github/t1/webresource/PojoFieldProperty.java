package com.github.t1.webresource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import com.github.t1.stereotypes.Annotations;

public class PojoFieldProperty extends PojoProperty {

    final Field field;

    public PojoFieldProperty(Field field) {
        super(Annotations.on(field));
        this.field = field;
    }

    @Override
    public Object of(Object object) {
        try {
            field.setAccessible(true);
            return field.get(object);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("can't get " + field.getName() + " of " + object, e);
        }
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("field ").append(field.getName());
        out.append(" of ").append(field.getDeclaringClass().getName());
        if (annotations.getAnnotations().length > 0) {
            out.append(": ");
            for (Annotation annotation : annotations.getAnnotations()) {
                out.append(annotation).append(" ");
            }
        }
        return out.toString();
    }

    @Override
    public String getName() {
        return field.getName();
    }
}
