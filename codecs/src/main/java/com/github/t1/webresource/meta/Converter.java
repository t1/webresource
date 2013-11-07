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
        throw new IllegalArgumentException("can't convert [" + value + "] to " + type.getSimpleName());
    }

    private boolean hasValueOf(Class<?> valueType) {
        return valueOfMethodOf(valueType) != null;
    }

    private Method valueOfMethodOf(Class<?> valueType) {
        for (Method method : type.getMethods()) {
            if (isValueOfMethod(method) && method.getParameterTypes()[0].isAssignableFrom(valueType)) {
                return method;
            }
        }
        return null;
    }

    private boolean isValueOfMethod(Method method) {
        return "valueOf".equals(method.getName()) //
                && Modifier.isStatic(method.getModifiers()) //
                && method.getParameterTypes().length == 1 //
                && method.getReturnType().isAssignableFrom(type);
    }

    private T valueOf(Object value) {
        try {
            return type.cast(valueOfMethodOf(value.getClass()).invoke(null, value));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
