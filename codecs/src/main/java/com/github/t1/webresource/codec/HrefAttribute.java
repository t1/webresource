package com.github.t1.webresource.codec;

import com.github.t1.webresource.codec.AbstractHtmlWriter.Attribute;
import com.github.t1.webresource.meta.Item;

public class HrefAttribute extends Attribute {

    private static String href(UriResolver uriResolver, Item item) {
        return uriResolver.resolveBase(item.type() + "/" + HtmlId.of(item) + ".html").toString();
    }

    public HrefAttribute(UriResolver uriResolver, Item item) {
        super("href", href(uriResolver, item));
    }
}
