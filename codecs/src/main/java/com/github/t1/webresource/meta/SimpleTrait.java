package com.github.t1.webresource.meta;

public class SimpleTrait extends AbstractTrait {
    // TODO SIMPLE constant -> constructor with class
    public static final Trait SIMPLE = new SimpleTrait();

    @Override
    public String name() {
        return "value";
    }

    @Override
    public Object of(Object object) {
        return object;
    }

    @Override
    public String type() {
        return "string";
    }
}
