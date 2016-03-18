package com.github.t1.webresource.util;

import java.lang.reflect.*;
import java.util.Collection;

public class Types {
    public static boolean isGenericCollection(Type type) {
        return type instanceof ParameterizedType
                && Collection.class.isAssignableFrom(raw((ParameterizedType) type));
    }

    public static Class<?> raw(ParameterizedType type) {
        return (Class<?>) type.getRawType();
    }

    public static Type nonCollectionType(Type type) {
        return isGenericCollection(type) ? elementType((ParameterizedType) type) : type;
    }

    public static Type elementType(ParameterizedType type) {
        return type.getActualTypeArguments()[0];
    }
}
