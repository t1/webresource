package com.github.t1.webresource.meta2;

import java.net.URI;
import java.util.*;

import javax.inject.Inject;

public class Items {
    @Inject
    private MapAccessor mapAccessor;
    @Inject
    private ListAccessor listAccessor;

    public Item of(Object object) {
        if (object instanceof List) {
            @SuppressWarnings("unchecked")
            List<Item> list = (List<Item>) object;
            return new ListSequence(list, this, listAccessor);
        } else if (object instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) object;
            return new MapCompound(map, mapAccessor, this);
        } else if (isPrimitive(object)) {
            return new Primitive<>(object);
        } else {
            return new PojoCompound(object, this);
        }
    }

    private boolean isPrimitive(Object object) {
        return object instanceof Boolean || object instanceof Character || object instanceof Number
                || object instanceof String || object instanceof URI;
    }
}
