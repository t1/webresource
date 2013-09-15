package com.github.t1.webresource.meta;

import java.util.List;


class PojoItem extends AbstractPojoItem {
    public PojoItem(Object object) {
        super(object);
    }

    @Override
    public List<Trait> traits() {
        if (traits == null) {
            this.traits = new PojoTraits(type);
        }
        return traits;
    }

    @Override
    public String toString() {
        return object.toString();
    }
}
