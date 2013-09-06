package com.github.t1.webresource.codec;

import java.io.IOException;

import com.github.t1.webresource.meta.*;

public class HtmlFormWriter extends AbstractHtmlWriter {

    private final Item item;

    public HtmlFormWriter(AbstractHtmlWriter context, Item item) {
        super(context);
        this.item = item;
    }

    public void write() throws IOException {
        for (Trait trait : item.traits()) {
            try (Tag div = new Tag("div")) {
                String name = trait.getName();
                String id = id(name);
                try (Tag label = new Tag("label", new Attribute("for", id), new Attribute("class", name + "-label"))) {
                    escaped().write(name);
                }
                writeField(item, trait, id);
            }
        }
    }
}
