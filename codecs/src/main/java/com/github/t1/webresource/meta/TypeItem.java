package com.github.t1.webresource.meta;

import java.util.*;

import com.github.t1.webresource.WebResourceTypeInfo;

public class TypeItem extends AbstractItem {

    private final Class<?> type;

    public TypeItem(Object type) {
        super(type);
        this.type = (Class<?>) type;
    }

    @Override
    public boolean isType() {
        return true;
    }

    @Override
    public String type() {
        return new WebResourceTypeInfo(type.getSimpleName()).plural;
    }

    @Override
    protected Collection<Trait> fetchAllTraits() {
        return Collections.emptySet();
    }

    @Override
    public String toString() {
        return type();
    }
}
