package com.github.t1.webresource.meta;

public class SimpleTrait extends ObjectTrait {
    public static SimpleTrait of(Item item) {
        return (SimpleTrait) item.traits().get(0);
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
    public Object of(Object object) {
        return object;
    }

    @Override
    public String toString() {
        return "SimpleTrait[" + type() + "]";
    }
}
