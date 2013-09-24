package com.github.t1.webresource.meta;

import java.util.*;

class SimplePojoItem extends AbstractPojoItem {
    private final Trait trait;

    public SimplePojoItem(Object object) {
        super(object);
        Class<?> type = (object == null) ? null : object.getClass();
        this.trait = new SimpleTrait(type);
    }

    @Override
    public List<Trait> traits() {
        return Collections.singletonList(trait);
    }

    @Override
    public boolean isSimple() {
        return true;
    }

    @Override
    public boolean isNull() {
        return object == null;
    }

    @Override
    public String toString() {
        return (object == null) ? "" : object.toString();
    }
}
