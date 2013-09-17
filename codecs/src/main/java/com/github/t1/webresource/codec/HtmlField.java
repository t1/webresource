package com.github.t1.webresource.codec;

import lombok.extern.slf4j.Slf4j;

import com.github.t1.webresource.meta.*;

/**
 * Renders a {@link Trait}... eventually as an <code>input</code>.
 */
@Slf4j
public class HtmlField implements CharSequence {

    private final Item item;
    private final Trait trait;
    private final String id;

    public HtmlField(Item item, Trait trait, String id) {
        this.item = item;
        this.trait = trait;
        this.id = id;
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
        StringBuilder result = new StringBuilder();
        result.append("<input");
        if (id != null)
            result.append(" id='" + id + "'");
        result.append(" class='" + cssClass() + "'");
        result.append(" type='" + inputType() + "'");
        result.append(" value='" + item + "' readonly");
        result.append("/>\n");
        return result.toString();
    }

    private String cssClass() {
        return trait.name();
    }

    private String inputType() {
        if (trait.is(HtmlInputType.class))
            return trait.get(HtmlInputType.class).value();
        String itemType = item.type();
        if (itemType == null)
            return "text";
        switch (itemType) {
            case "booleans":
                return "checkbox";
            case "integers":
            case "strings":
                return "text";
            default:
                log.debug("unknown item type [" + itemType + "] for field. Default to 'text'");
                return "text";
        }
    }
}
