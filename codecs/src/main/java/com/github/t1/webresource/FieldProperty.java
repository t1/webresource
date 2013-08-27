package com.github.t1.webresource;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import com.github.t1.stereotypes.Annotations;

public class FieldProperty implements Property {

    public static void addTo(List<Property> properties, Class<?> type) {
        for (Field field : type.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)
                    || field.isAnnotationPresent(XmlTransient.class))
                continue;
            properties.add(new FieldProperty(field));
        }
    }

    private final Field field;
    private final AnnotatedElement annotations;

    private FieldProperty(Field field) {
        this.field = field;
        this.annotations = Annotations.on(field);
    }

    @Override
    public Object of(Object object) {
        return getValue(object);
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
    public <T extends Annotation> boolean is(Class<T> type) {
        return annotations.isAnnotationPresent(type);
    }

    @Override
    public <T extends Annotation> T get(Class<T> type) {
        return annotations.getAnnotation(type);
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
