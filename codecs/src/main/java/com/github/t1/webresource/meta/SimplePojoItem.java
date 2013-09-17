package com.github.t1.webresource.meta;

import java.util.*;

class SimplePojoItem extends AbstractPojoItem {
    private static final List<Trait> SIMPLE_TRAITS = Collections.<Trait> singletonList(SimpleTrait.SIMPLE);

    public SimplePojoItem(Object object) {
        super(object);
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

    @Override
    public String toString() {
        return (object == null) ? "" : object.toString();
    }
}
