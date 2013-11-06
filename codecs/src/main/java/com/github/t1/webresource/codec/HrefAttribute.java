package com.github.t1.webresource.codec;

import javax.inject.Inject;

import com.github.t1.webresource.codec.HtmlOut.Attribute;
import com.github.t1.webresource.meta.Item;

public class HrefAttribute {

    @Inject
    UriResolver uriResolver;

    public Attribute to(Item item) {
        return new Attribute("href", String.valueOf(uriResolver.resolveBase(href(item))));
    }

    private String href(Item item) {
        if (item.isType())
            return item.type();
        return item.type() + "/" + HtmlId.of(item);
    }
}
