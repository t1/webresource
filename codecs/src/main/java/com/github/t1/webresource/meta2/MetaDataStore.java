package com.github.t1.webresource.meta2;

import java.util.*;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class MetaDataStore {
    private final Map<Object, Object> map = new IdentityHashMap<>();

    @SuppressWarnings("unchecked")
    public <T> T get(Object key) {
        return (T) map.get(key);
    }

    public <T> void put(T key, MetaData<T> value) {
        map.put(key, value);
    }
}
