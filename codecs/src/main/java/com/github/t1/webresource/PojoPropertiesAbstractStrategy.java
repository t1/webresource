package com.github.t1.webresource;

import java.lang.reflect.*;

public abstract class PojoPropertiesAbstractStrategy {

    protected final Class<?> type;
    protected final PojoProperties target;

    public PojoPropertiesAbstractStrategy(Class<?> type, PojoProperties target) {
        this.type = type;
        this.target = target;
    }

    public void run() {
        addFields();
        addGetters();
    }

    private void addFields() {
        for (Field field : type.getDeclaredFields()) {
            PojoFieldProperty property = new PojoFieldProperty(field);
            if (pass(property)) {
                target.add(property);
            }
        }
    }

    protected abstract boolean pass(PojoFieldProperty field);

    private void addGetters() {
        for (Method method : type.getDeclaredMethods()) {
            PojoGetterProperty getter = new PojoGetterProperty(method, name(method));
            if (pass(getter)) {
                target.add(getter);
            }
        }
    }

    protected abstract String name(Method method);

    protected abstract boolean pass(PojoGetterProperty getter);

    protected boolean hasProperty(String name) {
        for (Property property : target) {
            if (property.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
