package com.github.t1.webresource;

import java.lang.reflect.*;


public class PojoPropertiesDefaultStrategy extends PojoPropertiesAbstractStrategy {
    /** is not static and not transient */
    public static boolean isPublicMember(Member member) {
        int modifiers = member.getModifiers();
        return !Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers) && Modifier.isPublic(modifiers);
    }

    public PojoPropertiesDefaultStrategy(Class<?> type, PojoProperties target) {
        super(type, target);
    }

    @Override
    protected boolean pass(PojoFieldProperty field) {
        return isPublicMember(field.field);
    }

    @Override
    protected boolean pass(PojoGetterProperty getter) {
        return isPublicMember(getter.method) && isGetter(getter.method) && !hasProperty(getter.getName());
    }

    private boolean isGetter(Method method) {
        return method.getParameterTypes().length == 0 && method.getReturnType() != void.class
                && isGetterMethodName(method.getName());
    }

    private boolean isGetterMethodName(String name) {
        return name.startsWith("get") || name.startsWith("is");
    }

    @Override
    protected String name(Method method) {
        String name = method.getName();
        if (name.startsWith("get"))
            name = lowerFirst(name.substring(3));
        else if (name.startsWith("is"))
            name = lowerFirst(name.substring(2));
        return name;
    }

    private String lowerFirst(String name) {
        if (name.length() >= 1)
            name = name.substring(0, 1).toLowerCase() + name.substring(1);
        return name;
    }
}
