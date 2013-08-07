package com.github.t1.webresource;

import java.util.Objects;

/**
 * Renders a {@link Property}... eventually as an <code>input</code>.
 */
public class HtmlField implements CharSequence {

    private final Holder<?> holder;
    private final Property property;
    private String id;

    public HtmlField(Holder<?> holder, Property property) {
        this.holder = holder;
        this.property = property;
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
            return Objects.toString(holder.get(property));
        StringBuilder result = new StringBuilder();
        Object value = holder.get(property);
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
