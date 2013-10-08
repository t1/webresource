package com.github.t1.webresource.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;

import com.github.t1.stereotypes.Annotations;

public class PojoAccessorTrait extends PojoTrait {
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

    private final Method getter;

    public PojoAccessorTrait(Method method) {
        this(method, name(method));
    }

    private PojoAccessorTrait(Method method, String name) {
        super(collectAnnotations(method, name), name);
        this.getter = method;
    }

    private static String uncapitalize(String name) {
        if (name.length() < 1)
            return name;
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    @Override
    protected Method member() {
        return getter;
    }

    @Override
    protected Class<?> typeClass() {
        return getter.getReturnType();
    }

    @Override
    Object of(Object object) {
        try {
            getter.setAccessible(true);
            return getter.invoke(object);
        } catch (IllegalArgumentException | ReflectiveOperationException e) {
            throw new RuntimeException("can't get " + name() + " of " + object, e);
        }
    }

    @Override
    void set(Object object, Object value) {
        try {
            Method setter = setter();
            setter.setAccessible(true);
            setter.invoke(object, value);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("can't set " + name() + " of " + object + " to " + value, e);
        }
    }

    private Method setter() {
        String setterName = "set" + initCap(name());
        try {
            Class<?> declaringClass = getter.getDeclaringClass();
            return declaringClass.getDeclaredMethod(setterName, typeClass());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private String initCap(String name) {
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("getter ").append(name());
        out.append(" of ").append(getter.getDeclaringClass().getName());
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
        return getter.getParameterTypes().length == 0 && getter.getReturnType() != void.class
                && isGetterMethodName(getter.getName());
    }

    private boolean isGetterMethodName(String name) {
        return name.startsWith("get") || name.startsWith("is");
    }
}
