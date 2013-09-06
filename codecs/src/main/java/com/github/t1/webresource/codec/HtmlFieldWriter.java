package com.github.t1.webresource.codec;

import static com.github.t1.webresource.meta.SimpleTrait.*;

import java.io.*;
import java.net.URI;
import java.util.Objects;

import com.github.t1.webresource.meta.*;

public class HtmlFieldWriter extends AbstractHtmlWriter {
    private final Item item;
    private final Trait trait;
    private final String id;

    public HtmlFieldWriter(Writer out, URI baseUri, Item item, Trait trait, String id) {
        super(out, baseUri);
        this.item = item;
        this.trait = trait;
        this.id = id;
    }

    public void write() throws IOException {
        Object value = item.get(trait);
        if (value == null) {
            // append nothing
        } else if (item.isSimple()) {
            escaped().append(Objects.toString(value));
        } else {
            Item cellItem = Items.newItem(value);
            if (cellItem.isList()) {
                writeList(cellItem.getList(), SIMPLE);
            } else if (cellItem.traits().size() > 1) {
                try (Tag div = new Tag("div")) {
                    writeBody(cellItem);
                }
            } else {
                HtmlField field = new HtmlField(item, trait).id(id);
                append(field);
            }
        }
    }
}
