package com.github.t1.webresource.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;

import com.github.t1.stereotypes.Annotations;

public class PojoGetterTrait extends PojoTrait {
    private static AnnotatedElement collectAnnotations(Method method, String name) {
        AnnotatedElement methodAnnotations = Annotations.on(method);
        Field field = findField(method.getDeclaringClass(), name);
        if (field == null)
            return methodAnnotations;
        return new UnionAnnotatedElement(methodAnnotations, Annotations.on(field));
    }

    private static Field findField(Class<?> declaringClass, String name) {
        for (Field field : declaringClass.getDeclaredFields()) {
            if (field.getName().equals(name)) {
                return field;
            }
        }
        return null;
    }

    private static String name(Method method) {
        String name = method.getName();
        if (name.startsWith("get"))
            name = uncapitalize(name.substring(3));
        else if (name.startsWith("is"))
            name = uncapitalize(name.substring(2));
        return name;
    }

    private final Method method;

    public PojoGetterTrait(Method method) {
        this(method, name(method));
    }

    private PojoGetterTrait(Method method, String name) {
        super(collectAnnotations(method, name), name);
        this.method = method;
    }

    private static String uncapitalize(String name) {
        if (name.length() < 1)
            return name;
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }


    @Override
    protected Method member() {
        return method;
    }

    @Override
    protected Class<?> typeClass() {
        return method.getReturnType();
    }

    @Override
    public Object of(Object object) {
        try {
            method.setAccessible(true);
            return method.invoke(object);
        } catch (IllegalArgumentException | ReflectiveOperationException e) {
            throw new RuntimeException("can't get " + name() + " of " + object, e);
        }
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("getter ").append(name());
        out.append(" of ").append(method.getDeclaringClass().getName());
        if (annotations.getAnnotations().length > 0) {
            out.append(": ");
            for (Annotation annotation : annotations.getAnnotations()) {
                out.append(annotation).append(" ");
            }
        }
        return out.toString();
    }

    /**
     * Is this method a getter? I.e. the name starts with <code>get</code> (or <code>is</code> for booleans), returns a
     * type, and takes no arguments.
     */
    public boolean isGetter() {
        return method.getParameterTypes().length == 0 && method.getReturnType() != void.class
                && isGetterMethodName(method.getName());
    }

    private boolean isGetterMethodName(String name) {
        return name.startsWith("get") || name.startsWith("is");
    }
}
