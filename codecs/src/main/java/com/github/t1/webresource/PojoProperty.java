package com.github.t1.webresource;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Objects;

import javax.xml.bind.annotation.XmlTransient;

import com.github.t1.stereotypes.Annotations;

public class PojoProperty {

    public static final PojoProperty SIMPLE = new PojoProperty(null);

    private final Field field;
    private final AnnotatedElement annotations;

    public PojoProperty(Field field) {
        this.field = field;
        this.annotations = Annotations.on(field);
    }

    public <T extends Annotation> boolean is(Class<T> type) {
        return annotations.isAnnotationPresent(type);
    }

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

    public boolean isTransient() {
        int modifiers = field.getModifiers();
        return Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || is(XmlTransient.class);
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("field ");
        out.append(field.getName());
        if (annotations.getAnnotations().length > 0) {
            out.append(": ");
            for (Annotation annotation : annotations.getAnnotations()) {
                out.append(annotation).append(" ");
            }
        }
        return out.toString();
    }

    public String getName() {
        return field.getName();
    }
}
