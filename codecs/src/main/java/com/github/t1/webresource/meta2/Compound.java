package com.github.t1.webresource.meta2;

import lombok.Value;
import lombok.experimental.Accessors;

public interface Compound extends Item {
    @Value
    @Accessors(fluent = true)
    public static class Property {
        Primitive<String> name;
        Item value;
    }

    Iterable<Property> properties();

    Primitive<String> keyTitle();

    Primitive<String> valueTitle();
}
