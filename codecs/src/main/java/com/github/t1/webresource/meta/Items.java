package com.github.t1.webresource.meta;

public class Items {
    @SuppressWarnings("unchecked")
    public static <T> Item newItem(T object) {
        return new PojoItem((Class<T>) ((object == null) ? null : object.getClass()), object);
    }
}
