package com.github.t1.webresource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.github.t1.stereotypes.Annotations;

public class PojoGetterTrait extends PojoTrait {
    private final Method method;

    public PojoGetterTrait(Method method) {
        super(Annotations.on(method), name(method));
        this.method = method;
    }

    /** This is only the default name, subclasses can call {@link PojoGetterTrait#setName(String)} */
    private static String name(Method method) {
        String name = method.getName();
        if (name.startsWith("get"))
            name = uncapitalize(name.substring(3));
        else if (name.startsWith("is"))
            name = uncapitalize(name.substring(2));
        return name;
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
    public Object of(Object object) {
        try {
            method.setAccessible(true);
            return method.invoke(object);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("can't get " + getName() + " of " + object, e);
        }
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
