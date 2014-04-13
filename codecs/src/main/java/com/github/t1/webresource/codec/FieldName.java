package com.github.t1.webresource.codec;

import com.github.t1.webresource.meta.Trait;

public class FieldName implements CharSequence {
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

    @Override
    public int length() {
        return name.length();
    }

    @Override
    public char charAt(int index) {
        return name.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return name.subSequence(start, end);
    }
}
