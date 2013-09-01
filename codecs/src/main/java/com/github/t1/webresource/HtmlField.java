package com.github.t1.webresource;

import java.util.Objects;

/**
 * Renders a {@link Trait}... eventually as an <code>input</code>.
 */
public class HtmlField implements CharSequence {

    private final Item<?> item;
    private final Trait trait;
    private String id;

    public HtmlField(Item<?> item, Trait trait) {
        this.item = item;
        this.trait = trait;
    }

    public HtmlField id(String id) {
        this.id = id;
        return this;
    }

    @Override
    public int length() {
        return toString().length();
    }

    @Override
    public char charAt(int index) {
        return toString().charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return toString().subSequence(start, end);
    }

    @Override
    public String toString() {
        if (id == null)
            return Objects.toString(item.get(trait));
        StringBuilder result = new StringBuilder();
        Object value = item.get(trait);
        result.append("<input");
        if (id != null)
            result.append(" id='" + id + "'");
        result.append(" type='" + typeFor(value) + "'");
        if (value != null)
            result.append(" value='" + value + "' readonly");
        result.append("/>\n");
        return result.toString();
    }

    private String typeFor(Object value) {
        if (value instanceof Boolean)
            return "checkbox";
        return "text";
    }
}
