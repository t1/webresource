package com.github.t1.webresource.meta;

class PojoItem extends AbstractItem {
    public PojoItem(Object object) {
        super(object);
    }

    @Override
    protected PojoTraits fetchAllTraits() {
        return new PojoTraits(type);
    }

    @Override
    public String toString() {
        return object.toString();
    }
}
