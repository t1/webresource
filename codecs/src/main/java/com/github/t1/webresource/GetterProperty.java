package com.github.t1.webresource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.github.t1.stereotypes.Annotations;

public class GetterProperty extends PojoProperty {
    private final Method method;
    private final String name;

    public GetterProperty(Method method) {
        super(Annotations.on(method));
        this.method = method;
        this.name = name(method);
    }

    private String name(Method method) {
        String name = method.getName().substring(3);
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    @Override
    public Object of(Object object) {
        try {
            method.setAccessible(true);
            return method.invoke(object);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("can't get " + method.getName() + " of " + object, e);
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

    @Override
    public String getName() {
        return name;
    }
}
