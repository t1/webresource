package com.github.t1.webresource.meta2;

import lombok.Value;

import com.github.t1.webresource.meta2.Compound.Entry;

public interface Compound extends Item, Iterable<Entry> {
    @Value
    public static class Entry {
        Primitive key;
        Item value;
    }

    Primitive keyTitle();

    Primitive valueTitle();
}
