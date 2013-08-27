package com.github.t1.webresource;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;

import javax.xml.bind.annotation.XmlTransient;

import com.github.t1.stereotypes.Annotations;

public class GetterProperty implements Property {
    public static GetterProperty of(Method method) {
        if (isTransient(method) || isPublic(method) || !isGetter(method))
            return null;
        return new GetterProperty(method);
    }

    private static boolean isTransient(Method method) {
        int modifiers = method.getModifiers();
        return Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)
                || method.isAnnotationPresent(XmlTransient.class);
    }

    private static boolean isPublic(Method method) {
        return Modifier.isPublic(method.getModifiers());
    }

    private static boolean isGetter(Method method) {
        return method.getParameterTypes().length == 0 && method.getReturnType() != void.class
                && method.getName().startsWith("get");
    }

    private final Method method;
    private final AnnotatedElement annotations;

    private GetterProperty(Method method) {
        this.method = method;
        this.annotations = Annotations.on(method);
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
        out.append("method ").append(method.getName());
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
        return method.getName();
    }
}
