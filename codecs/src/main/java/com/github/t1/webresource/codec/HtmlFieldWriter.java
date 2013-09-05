package com.github.t1.webresource.codec;

import static com.github.t1.webresource.meta.SimpleTrait.*;

import java.io.IOException;
import java.util.Objects;

import com.github.t1.webresource.meta.*;

public class HtmlFieldWriter extends AbstractHtmlWriter {
    private final Item item;
    private final Trait trait;
    private final String id;

    public HtmlFieldWriter(HtmlWriter out, Item item, Trait trait, String id) {
        super(out);
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
                new HtmlListWriter(out, cellItem.getList(), SIMPLE).write();
            } else if (cellItem.traits().size() > 1) {
                try (Tag div = new Tag("div")) {
                    new HtmlBodyWriter(out, cellItem).write();
                }
            } else {
                HtmlField field = new HtmlField(item, trait).id(id);
                out.append(field);
            }
        }
    }
}
