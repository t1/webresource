package com.github.t1.webresource.codec;

import lombok.Delegate;

import com.github.t1.webresource.meta.Trait;

public class FieldName implements CharSequence {
    @Delegate
    private final CharSequence name;

    public FieldName(Trait trait) {
        this.name = name(trait);
    }

    private String name(Trait trait) {
        if (trait.is(HtmlFieldName.class))
            return trait.get(HtmlFieldName.class).value();
        return trait.name();
    }

    @Override
    public String toString() {
        return name.toString();
    }
}
