package com.github.t1.webresource.meta;

import java.lang.reflect.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Converter<T> {
    public static <T> Converter<T> to(Class<T> type) {
        return new Converter<>(type);
    }

    private final Class<T> type;

    private Converter(Class<T> type) {
        this.type = type;
    }

    public T convert(Object value) {
        log.debug("converting [{}] to {}", value, type.getSimpleName());
        if (type.isInstance(value))
            return type.cast(value);
        if (hasValueOf(value.getClass()))
            return valueOf(value);
        return null;
    }

    private boolean hasValueOf(Class<?> valueType) {
        return valueOfMethodOf(valueType) != null;
    }

    private Method valueOfMethodOf(Class<?> valueType) {
        try {
            Method method = type.getMethod("valueOf", valueType);
            if (!Modifier.isStatic(method.getModifiers()))
                return null;
            return method;
        } catch (NoSuchMethodException e) {
            return null; // TODO this may be slow
        }
    }

    private T valueOf(Object value) {
        try {
            return type.cast(valueOfMethodOf(type).invoke(null, value));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
