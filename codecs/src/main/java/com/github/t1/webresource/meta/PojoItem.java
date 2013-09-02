package com.github.t1.webresource.meta;

import java.util.List;


class PojoItem extends AbstractPojoItem {
    public <T> PojoItem(Class<T> type, T object) {
        super(type, object);
    }

    @Override
    public List<Trait> traits() {
        if (traits == null) {
            this.traits = new PojoTraits(type);
        }
        return traits;
    }
}
