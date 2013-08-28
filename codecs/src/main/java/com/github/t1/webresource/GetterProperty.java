package com.github.t1.webresource;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;

import com.github.t1.stereotypes.Annotations;

public class GetterProperty implements Property {
    private final Method method;
    private final String name;
    private final AnnotatedElement annotations;

    public GetterProperty(Method method) {
        this.method = method;
        this.name = name(method);
        this.annotations = Annotations.on(method);
    }

    private String name(Method method) {
        String name = method.getName().substring(3);
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    @Override
    public Object of(Object object) {
        return getValue(object);
    }

    private Object getValue(Object object) {
        try {
            method.setAccessible(true);
            return method.invoke(object);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("can't get " + method.getName() + " of " + object, e);
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
        out.append("getter ").append(getName());
        out.append(" of ").append(method.getDeclaringClass().getName());
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
        return name;
    }
}
