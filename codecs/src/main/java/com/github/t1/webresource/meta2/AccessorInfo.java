package com.github.t1.webresource.meta2;

import java.lang.reflect.*;
import java.util.*;

public class AccessorInfo {
    private final Class<?> accessorType;
    private final Map<TypeVariable<?>, Type> typeVariables = new HashMap<>();

    public AccessorInfo(Accessor<?> accessor) {
        this.accessorType = accessor.getClass();
    }

    public Class<?> type() {
        return type(accessorType);
    }

    public Class<?> type(Class<?> type) {
        for (Type interfaceType : type.getGenericInterfaces()) {
            if (interfaceType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) interfaceType;
                if (Accessor.class.equals(parameterizedType.getRawType())) {
                    Type genericArgument = parameterizedType.getActualTypeArguments()[0];
                    genericArgument = resolveTypeVariable(genericArgument);
                    if (genericArgument instanceof ParameterizedType)
                        genericArgument = ((ParameterizedType) genericArgument).getRawType();
                    if (genericArgument instanceof Class) {
                        return (Class<?>) genericArgument;
                    }
                }
            }
            if (isRawAccessor(interfaceType)) {
                throw new IllegalArgumentException("the accessor " + type
                        + " doesn't declare the type variable (i.e. it's raw), but the type info is required.");
            }
        }
        try {
            Class<?> superClass = type.getSuperclass();
            if (superClass != null) {
                addTypeVariables(type);
                return type(superClass);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("can't get type info for " + type, e);
        }
        throw new IllegalArgumentException("no geric type info found for " + type);
    }

    private void addTypeVariables(Class<?> accessorType) {
        Type superType = accessorType.getGenericSuperclass();
        if (superType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) superType;
            addTypeVariable(accessorType, parameterizedType);
        }
    }

    private void addTypeVariable(Class<?> accessorType, ParameterizedType parameterizedType) {
        TypeVariable<?>[] typeParameters = accessorType.getSuperclass().getTypeParameters();
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        for (int i = 0; i < typeParameters.length; i++) {
            typeVariables.put(typeParameters[i], actualTypeArguments[i]);
        }
    }

    private Type resolveTypeVariable(Type type) {
        if (type instanceof TypeVariable) {
            TypeVariable<?> typeVariable = (TypeVariable<?>) type;
            return typeVariables.get(typeVariable);
        }
        return type;
    }

    private boolean isRawAccessor(Type type) {
        return Accessor.class.equals(type);
    }
}
