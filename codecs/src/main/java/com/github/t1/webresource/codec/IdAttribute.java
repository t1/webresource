package com.github.t1.webresource.codec;

import com.github.t1.webresource.codec.AbstractHtmlWriter.Attribute;
import com.github.t1.webresource.meta.Item;

public class IdAttribute extends Attribute {
    public IdAttribute(Item item) {
        this(item, null);
    }

    public IdAttribute(Item item, String suffix) {
        super("id", item.type() + ((suffix == null) ? "" : ("-" + suffix)));
    }
}
