package com.github.t1.webresource;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Objects;

import javax.xml.bind.annotation.XmlTransient;

import com.github.t1.stereotypes.Annotations;

public class FieldProperty implements Property {

    public static final FieldProperty SIMPLE = new FieldProperty(null);

    private final Field field;
    private final AnnotatedElement annotations;

    public FieldProperty(Field field) {
        this.field = field;
        this.annotations = Annotations.on(field);
    }

    @Override
    public <T extends Annotation> boolean is(Class<T> type) {
        return annotations.isAnnotationPresent(type);
    }

    @Override
    public String of(Object object) {
        Object value = (SIMPLE == this) ? object : getValue(object);
        return (value == null) ? null : Objects.toString(value);
    }

    private Object getValue(Object object) {
        try {
            field.setAccessible(true);
            return field.get(object);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("can't get " + field.getName() + " of " + object, e);
        }
    }

    @Override
    public boolean isTransient() {
        int modifiers = field.getModifiers();
        return Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || is(XmlTransient.class);
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
