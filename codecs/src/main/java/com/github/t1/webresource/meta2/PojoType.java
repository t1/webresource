package com.github.t1.webresource.meta2;

import static java.util.Collections.*;

import java.lang.reflect.*;
import java.util.*;

import javax.xml.bind.annotation.XmlTransient;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PojoType {
    private static Map<Class<?>, PojoType> CACHE = new HashMap<>();

    public static PojoType of(Class<?> type) {
        PojoType pojoType = CACHE.get(type);
        if (pojoType == null) {
            pojoType = new PojoType(type);
            CACHE.put(type, pojoType);
        }
        return pojoType;
    }

    @Accessors(fluent = true)
    public static class PojoProperty {
        @Getter
        private final String name;
        private final Method method;

        public PojoProperty(Method method) {
            this.method = method;
            this.name = name(method);
        }

        private String name(Method method) {
            String name = method.getName();
            if (name.length() <= 3)
                return "";
            return Character.toLowerCase(name.charAt(3)) + name.substring(4);
        }

        public Object get(Object target) {
            try {
                return method.invoke(target);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        public boolean valid() {
            return isGetter() && method.getDeclaringClass() != Object.class
                    && !Modifier.isStatic(method.getModifiers()) && !isTransient();
        }

        private boolean isGetter() {
            return method.getParameterTypes().length == 0 && method.getReturnType() != void.class
                    && isGetterName(method.getName());
        }

        private boolean isGetterName(String name) {
            return name.startsWith("get") && name.length() > 3 && Character.isUpperCase(name.charAt(3));
        }

        private boolean isTransient() {
            return Modifier.isTransient(field().getModifiers()) || field().isAnnotationPresent(XmlTransient.class);
        }

        private Field field() {
            try {
                return method.getDeclaringClass().getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Getter
    @Accessors(fluent = true)
    private final List<PojoProperty> pojoProperties;

    private PojoType(Class<?> type) {
        this.pojoProperties = pojoProperties(type);
    }

    private List<PojoProperty> pojoProperties(Class<?> type) {
        List<PojoProperty> list = new ArrayList<>();
        for (Method method : type.getMethods()) {
            PojoProperty pojoProperty = new PojoProperty(method);
            if (pojoProperty.valid()) {
                log.debug("found property {}", pojoProperty.name());
                list.add(pojoProperty);
            }
        }
        reorder(list, type);
        return list;
    }

    private void reorder(List<PojoProperty> list, Class<?> type) {
        final List<String> order = fieldOrder(type);
        sort(list, new Comparator<PojoProperty>() {
            @Override
            public int compare(PojoProperty left, PojoProperty right) {
                return index(left.name()) - index(right.name());
            }

            private int index(String string) {
                return order.indexOf(string);
            }
        });
    }

    private List<String> fieldOrder(Class<?> type) {
        List<String> fieldNames = new ArrayList<>();
        for (Field field : type.getDeclaredFields()) {
            fieldNames.add(field.getName());
        }
        log.debug("field order: {}", fieldNames);
        return fieldNames;
    }
}
