package com.github.t1.webresource.meta;

import static com.github.t1.webresource.meta.Converter.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;

import com.google.common.base.Predicate;

public class PojoAccessorTrait extends PojoTrait {
    private final Method getter;

    public PojoAccessorTrait(Method method, AnnotatedElement annotations, String name, Predicate<PojoTrait> visible) {
        super(annotations, name, visible);
        this.getter = method;
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
            setter.invoke(object, to(setter.getParameterTypes()[0]).convert(value));
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
        out.append("accessor ").append(name());
        out.append(" of ").append(getter.getDeclaringClass().getName());
        if (annotations.getAnnotations().length > 0) {
            out.append(": ");
            for (Annotation annotation : annotations.getAnnotations()) {
                out.append(annotation).append(" ");
            }
        }
        return out.toString();
    }
}
