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
                target.add(property);
            }
        }
    }

    protected abstract boolean pass(PojoFieldProperty field);

    private void addGetters() {
        for (Method method : type.getDeclaredMethods()) {
            PojoGetterProperty getter = new PojoGetterProperty(method, name(method));
            if (getter.isPublicMember() && pass(getter)) {
                target.add(getter);
            }
        }
    }

    /** This is only the default name, subclasses can call {@link PojoGetterProperty#setName(String)} */
    private String name(Method method) {
        String name = method.getName();
        if (name.startsWith("get"))
            name = uncapitalize(name.substring(3));
        else if (name.startsWith("is"))
            name = uncapitalize(name.substring(2));
        return name;
    }

    private String uncapitalize(String name) {
        if (name.length() < 1)
            return name;
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

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
