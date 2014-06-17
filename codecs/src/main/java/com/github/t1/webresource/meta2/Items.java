package com.github.t1.webresource.meta2;

import java.util.*;

import javax.inject.Inject;

public class Items {
    @Inject
    private MapAccessor mapAccessor;

    public Item of(Object t) {
        if (t instanceof List)
            return new ListSequence((List<?>) t, this);
        if (t instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) t;
            return new MapCompound(map, mapAccessor, this);
        }
        return new Primitive(t);
    }
}
