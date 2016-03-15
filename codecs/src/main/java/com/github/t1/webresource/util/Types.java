package com.github.t1.webresource.util;

import com.github.t1.stereotypes.Annotations;

import java.lang.reflect.*;
import java.util.Collection;

public class Types {
    public static boolean isGenericCollection(Type type) {
        return type instanceof ParameterizedType
                && Collection.class.isAssignableFrom(raw((ParameterizedType) type));
    }

    public static Type nonCollectionType(Type type) {
        return isGenericCollection(type) ? elementType((ParameterizedType) type) : type;
    }

    public static Class<?> raw(ParameterizedType type) {
        return (Class<?>) type.getRawType();
    }

    public static Type elementType(ParameterizedType type) {
        return type.getActualTypeArguments()[0];
    }

    public static AnnotatedElement annotationsOn(Type type) { return Annotations.on((Class<?>) type); }
}
