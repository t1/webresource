package com.github.t1.webresource.codec;

import java.io.IOException;
import java.util.List;

import com.github.t1.webresource.meta.*;

public class HtmlListWriter extends AbstractHtmlWriter {

    private final List<Item> list;
    private final Trait trait;

    public HtmlListWriter(AbstractHtmlWriter context, List<Item> list, Trait trait) {
        super(context);
        this.list = list;
        this.trait = trait;
    }

    public void write() throws IOException {
        try (Tag ul = new Tag("ul")) {
            for (Item element : list) {
                try (Tag li = new Tag("li")) {
                    Item cellItem = element.get(trait);
                    writeLink(cellItem, "id", "");
                }
            }
        }
    }
}
