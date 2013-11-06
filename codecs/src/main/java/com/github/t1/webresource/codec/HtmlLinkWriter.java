package com.github.t1.webresource.codec;

import javax.inject.Inject;

import com.github.t1.webresource.codec.HtmlOut.Attribute;
import com.github.t1.webresource.codec.HtmlOut.Tag;
import com.github.t1.webresource.meta.Item;

public class HtmlLinkWriter {
    @Inject
    HtmlOut out;
    @Inject
    HrefAttribute href;
    @Inject
    HtmlTitleWriter titleWriter;

    public void write(Item item, String id) {
        try (Tag a = out.tag("a", href.to(item), idAttribute(id), new ClassAttribute(item))) {
            out.write(titleWriter.title(item));
        }
    }

    private Attribute idAttribute(String id) {
        return new Attribute("id", id + "-href");
    }
}
