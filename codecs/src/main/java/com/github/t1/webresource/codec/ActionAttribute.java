package com.github.t1.webresource.codec;

import com.github.t1.webresource.codec.AbstractHtmlWriter.Attribute;
import com.github.t1.webresource.meta.Item;

public class ActionAttribute extends Attribute {

    public ActionAttribute(UriResolver uriResolver, Item item) {
        super("action", uriResolver.resolveBase(item.type()).toString());
    }
}
