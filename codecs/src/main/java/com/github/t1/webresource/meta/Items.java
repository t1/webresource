package com.github.t1.webresource.meta;

public class Items<T> {
    @SuppressWarnings("unchecked")
    public static <T> Item newItem(T object) {
        Class<T> type = (Class<T>) ((object == null) ? null : object.getClass());
        return new Items<>(type, object).create();
    }

    private final Class<T> type;
    private final T object;

    public Items(Class<T> type, T object) {
        this.type = type;
        this.object = object;
    }

    private Item create() {
        if (isSimple())
            return new SimplePojoItem(type, object);
        return new PojoItem(type, object);
    }

    private boolean isSimple() {
        return isNull() || type == String.class || Number.class.isAssignableFrom(type) || type == Boolean.class
                || type.isPrimitive();
    }

    private boolean isNull() {
        return object == null;
    }
}
