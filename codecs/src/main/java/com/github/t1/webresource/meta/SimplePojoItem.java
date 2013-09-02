package com.github.t1.webresource.meta;

import java.util.*;

class SimplePojoItem extends AbstractPojoItem {
    private static final List<Trait> SIMPLE_TRAITS = Collections.<Trait> singletonList(SimpleTrait.SIMPLE);

    public <T> SimplePojoItem(Class<T> type, T object) {
        super(type, object);
    }

    @Override
    public List<Trait> traits() {
        return SIMPLE_TRAITS;
    }

    @Override
    public boolean isSimple() {
        return true;
    }

    @Override
    public boolean isNull() {
        return object == null;
    }
}
