package com.github.t1.webresource.meta;

import java.lang.annotation.Annotation;
import java.util.Map;


public class MapTrait extends AbstractTrait {

    private final String key;

    public MapTrait(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + key + "]";
    }

    @Override
    public String name() {
        return key;
    }

    @Override
    Object of(Object object) {
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return map.get(key);
    }

    @Override
    void set(Object object, Object value) {
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        map.put(key, value);
    }

    @Override
    public <T extends Annotation> boolean is(Class<T> type) {
        return false;
    }

    @Override
    public <T extends Annotation> T get(Class<T> type) {
        return null;
    }

    @Override
    public String type() {
        return "string";
    }
}
