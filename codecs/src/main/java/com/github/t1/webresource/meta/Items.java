package com.github.t1.webresource.meta;

import java.util.*;

public class Items<T> {
    public static Item newItem(Object object) {
        return new Items<>(object).create();
    }

    private final Class<T> type;
    private final T object;

    @SuppressWarnings("unchecked")
    public Items(T object) {
        this.object = object;
        this.type = (Class<T>) (isNull() ? null : object.getClass());
    }

    private Item create() {
        if (isSimple())
            return new SimplePojoItem(type, object);
        if (isMap())
            return new MapItem(type, object);
        if (isList())
            return new ListItem(type, object);
        return new PojoItem(type, object);
    }

    private boolean isSimple() {
        return isNull() || type == String.class || Number.class.isAssignableFrom(type) || type == Boolean.class
                || type.isPrimitive();
    }

    private boolean isNull() {
        return object == null;
    }

    private boolean isMap() {
        return Map.class.isAssignableFrom(type);
    }

    private boolean isList() {
        return List.class.isAssignableFrom(type);
    }
}
