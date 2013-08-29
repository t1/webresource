package com.github.t1.webresource;

import java.lang.reflect.Method;


public class PojoPropertiesDefaultStrategy extends PojoPropertiesAbstractStrategy {
    public PojoPropertiesDefaultStrategy(Class<?> type, PojoProperties target) {
        super(type, target);
    }

    @Override
    protected boolean pass(PojoFieldProperty field) {
        return true;
    }

    @Override
    protected boolean pass(PojoGetterProperty getter) {
        return isGetter(getter.method) && !hasProperty(getter.getName());
    }

    private boolean isGetter(Method method) {
        return method.getParameterTypes().length == 0 && method.getReturnType() != void.class
                && isGetterMethodName(method.getName());
    }

    private boolean isGetterMethodName(String name) {
        return name.startsWith("get") || name.startsWith("is");
    }
}
