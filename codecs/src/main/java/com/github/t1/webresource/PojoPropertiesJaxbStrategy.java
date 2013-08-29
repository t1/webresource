package com.github.t1.webresource;

import java.lang.reflect.Method;


public class PojoPropertiesJaxbStrategy extends PojoPropertiesAbstractStrategy {
    public PojoPropertiesJaxbStrategy(Class<?> type, PojoProperties target) {
        super(type, target);
    }

    @Override
    protected boolean pass(PojoFieldProperty field) {
        return true;
    }

    @Override
    protected boolean pass(PojoGetterProperty getter) {
        return false;
    }

    @Override
    protected String name(Method method) {
        return method.getName();
    }
}
