package com.github.t1.webresource;

import java.lang.annotation.Annotation;
import java.util.Map;


public class MapProperty implements Property {

    private final String key;

    public MapProperty(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + key + "]";
    }

    @Override
    public String getName() {
        return key;
    }

    @Override
    public Object of(Object object) {
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return map.get(key);
    }

    @Override
    public <T extends Annotation> boolean is(Class<T> type) {
        return false;
    }

    @Override
    public <T extends Annotation> T get(Class<T> type) {
        return null;
    }
}
