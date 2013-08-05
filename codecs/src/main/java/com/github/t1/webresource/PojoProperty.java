package com.github.t1.webresource;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Objects;

import javax.xml.bind.annotation.XmlTransient;

import com.github.t1.stereotypes.Annotations;

public class PojoProperty {

    private final Object object;
    private final Field field;
    private final AnnotatedElement annotations;

    public PojoProperty(Object object, Field field) {
        this.object = object;
        this.field = field;
        this.annotations = Annotations.on(field);
    }

    public <T extends Annotation> boolean is(Class<T> type) {
        return annotations.isAnnotationPresent(type);
    }

    public String get() {
        try {
            field.setAccessible(true);
            return Objects.toString(field.get(object));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("can't get " + field.getName(), e);
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
        out.append(" of ");
        out.append(object);
        if (annotations.getAnnotations().length > 0) {
            out.append(": ");
            for (Annotation annotation : annotations.getAnnotations()) {
                out.append(annotation).append(" ");
            }
        }
        return out.toString();
    }
}
