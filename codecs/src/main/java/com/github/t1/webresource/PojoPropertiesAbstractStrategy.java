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
            if (property.isPublicMember() && pass(property)) {
                init(property);
                target.add(property);
            }
        }
    }

    protected abstract boolean pass(PojoFieldProperty field);

    protected abstract boolean pass(PojoGetterProperty getter);

    /** esp. for overwriting the name after it was decided to add this property */
    protected void init(PojoProperty property) {}

    private void addGetters() {
        for (Method method : type.getDeclaredMethods()) {
            PojoGetterProperty getter = new PojoGetterProperty(method);
            if (getter.isPublicMember() && pass(getter)) {
                init(getter);
                target.add(getter);
            }
        }
    }

    protected boolean hasProperty(String name) {
        for (Property property : target) {
            if (property.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
