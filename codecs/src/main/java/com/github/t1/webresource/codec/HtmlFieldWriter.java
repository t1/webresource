package com.github.t1.webresource.codec;

import static com.github.t1.webresource.meta.SimpleTrait.*;

import java.io.IOException;

import com.github.t1.webresource.meta.*;

public class HtmlFieldWriter extends AbstractHtmlWriter {
    private final Item item;
    private final Trait trait;
    private final String id;

    public HtmlFieldWriter(AbstractHtmlWriter context, Item item, Trait trait, String id) {
        super(context);
        this.item = item;
        this.trait = trait;
        this.id = id;
    }

    public void write() throws IOException {
        if (item.isSimple()) {
            escaped().append(item.get(trait).toString());
        } else {
            Item cellItem = item.get(trait);
            if (cellItem.isList()) {
                writeList(cellItem.getList(), SIMPLE);
            } else {
                HtmlField field = new HtmlField(cellItem, trait.getName(), id);
                write(field);
            }
        }
    }
}
