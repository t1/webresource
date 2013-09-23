package com.github.t1.webresource.codec;

import static com.github.t1.webresource.meta.SimpleTrait.*;

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
            String name = name(trait);
            String id = id(name);
            try (Tag div = new Tag("div" /* TODO , new Attribute("class", name + "-item") */)) {
                try (Tag label = new Tag("label", new Attribute("for", id), new Attribute("class", name + "-label"))) {
                    escaped().write(name);
                }
                writeItem(trait, id);
            }
        }
    }

    private void writeItem(Trait trait, String id) throws IOException {
        Item value = item.get(trait);
        if (value.isSimple()) {
            writeField(item, trait, id);
        } else if (value.isList()) {
            writeList(value.getList(), SIMPLE);
        } else {
            writeLink(value, id);
        }
    }
}
