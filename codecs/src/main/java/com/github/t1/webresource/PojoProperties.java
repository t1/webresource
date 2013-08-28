package com.github.t1.webresource;

import java.lang.reflect.*;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlTransient;

/**
 * Although I generally prefer composition over inheritance, in this case I prefer the concise and intuitive syntax for
 * the client: <code>new PojoProperties(type)</code>
 */
public class PojoProperties extends ArrayList<Property> {
    private static final long serialVersionUID = 1L;

    public PojoProperties(Class<?> type) {
        addFields(type);
        addGetters(type);
    }

    private void addFields(Class<?> type) {
        for (Field field : type.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)
                    || field.isAnnotationPresent(XmlTransient.class))
                continue;
            add(new FieldProperty(field));
        }
    }

    private void addGetters(Class<?> type) {
        for (Method method : type.getDeclaredMethods()) {
            if (isTransient(method) || !isPublic(method) || !isGetter(method))
                continue;
            GetterProperty getter = new GetterProperty(method);
            if (!hasProperty(getter.getName())) {
                add(getter);
            }
        }
    }

    private boolean hasProperty(String name) {
        for (Property property : this) {
            if (property.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTransient(Method method) {
        int modifiers = method.getModifiers();
        return Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)
                || method.isAnnotationPresent(XmlTransient.class);
    }

    private boolean isPublic(Method method) {
        return Modifier.isPublic(method.getModifiers());
    }

    private boolean isGetter(Method method) {
        return method.getParameterTypes().length == 0 && method.getReturnType() != void.class
                && method.getName().startsWith("get");
    }
}
