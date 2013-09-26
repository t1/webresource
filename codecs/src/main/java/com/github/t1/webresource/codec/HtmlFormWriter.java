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
        try (Tag form = new Tag("form")) {
            for (Trait trait : item.traits()) {
                writeFormDiv(trait);
            }
        }
    }

    private void writeFormDiv(Trait trait) throws IOException {
        String name = name(trait);
        String id = id(name);
        try (Tag div = new Tag("div" /* TODO , new Attribute("class", name + "-item") */)) {
            try (Tag label =
                    new Tag("label", new Attribute("for", id), new Attribute("class", name + "-label"))) {
                escaped().write(name);
            }
            writeItem(trait, id);
        }
    }

    private void writeItem(Trait trait, String id) throws IOException {
        Item value = item.get(trait);
        if (value.isSimple()) {
            writeField(item, trait, id);
        } else if (value.isList()) {
            writeList(value.getList());
        } else {
            writeLink(value, id);
        }
    }
}
