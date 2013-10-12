package com.github.t1.webresource.meta;

import java.util.Collection;

public class SimpleTrait extends ObjectTrait {
    public static SimpleTrait of(Item item) {
        Collection<Trait> traits = item.traits();
        if (traits.size() != 1)
            throw new IllegalArgumentException("expected an item with exactly one trait, but found " + traits);
        return (SimpleTrait) traits.iterator().next();
    }

    private final Class<?> type;

    public SimpleTrait(Class<?> type) {
        this.type = type;
    }

    @Override
    public String name() {
        return "value";
    }

    @Override
    protected Class<?> typeClass() {
        return type;
    }

    @Override
    public boolean visible() {
        return true;
    }

    @Override
    Object of(Object object) {
        return object;
    }

    @Override
    void set(Object object, Object value) {
        throw new IllegalStateException("can't set a simple trait");
    }

    @Override
    public String toString() {
        return "SimpleTrait[" + type() + "]";
    }
}
