package com.github.t1.webresource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import lombok.*;

import com.github.t1.stereotypes.Annotations;

public class PojoGetterProperty extends PojoProperty {
    final Method method;
    @Getter
    @Setter
    private String name;

    /**
     * Note that the name of the method does not have to match the name of the property... not only that the "get"
     * prefix has to be removed, in JAXB there are annotations to set the name explicitly.
     */
    public PojoGetterProperty(Method method, String name) {
        super(Annotations.on(method));
        this.method = method;
        this.name = name;
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
}
