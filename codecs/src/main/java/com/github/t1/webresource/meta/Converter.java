package com.github.t1.webresource.meta;

import java.lang.reflect.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Converter {
    public static Converter convert(Object value) {
        return new Converter(value);
    }

    private final Object value;

    private Converter(Object value) {
        this.value = value;
    }

    public Object to(Class<?> type) {
        log.debug("converting [{}] to {}", value, type.getSimpleName());
        if (type.isInstance(value))
            return value;
        if (hasValueOf(type))
            return valueOfFor(type);
        return null;
    }

    private boolean hasValueOf(Class<?> type) {
        return valueOfMethodOf(type) != null;
    }

    private Method valueOfMethodOf(Class<?> type) {
        try {
            Method method = type.getMethod("valueOf", value.getClass());
            if (!Modifier.isStatic(method.getModifiers()))
                return null;
            return method;
        } catch (NoSuchMethodException e) {
            return null; // TODO this may be slow
        }
    }

    private Object valueOfFor(Class<?> type) {
        try {
            return valueOfMethodOf(type).invoke(null, value);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
