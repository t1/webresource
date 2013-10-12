package com.github.t1.webresource.meta;

import java.lang.reflect.AnnotatedElement;
import java.util.*;

class MapItem extends AbstractItem {
    public MapItem(Object object) {
        super(object);
    }

    @Override
    protected AnnotatedElement annotations() {
        return null;
    }

    @Override
    public List<Trait> fetchAllTraits() {
        List<Trait> traits = new ArrayList<>();
        @SuppressWarnings("unchecked")
        Map<String, ?> map = (Map<String, ?>) object;
        for (String key : map.keySet()) {
            traits.add(new MapTrait(key));
        }
        return traits;
    }
}
