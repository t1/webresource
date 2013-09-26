package com.github.t1.webresource.codec;

import java.io.IOException;
import java.util.List;

import com.github.t1.webresource.meta.*;

public class HtmlListWriter extends AbstractHtmlWriter {

    private final String type;
    private final List<Item> list;

    public HtmlListWriter(AbstractHtmlWriter context, Item listItem) {
        super(context);
        this.list = listItem.getList();
        this.type = list.isEmpty() ? "empty" : list.get(0).type();
    }

    public void write() throws IOException {
        try (Tag ul = new Tag("ul", new Attribute("class", type))) {
            for (Item item : list) {
                try (Tag li = new Tag("li")) {
                    if (item.isSimple()) {
                        write(item.get(SimpleTrait.of(item)));
                    } else {
                        writeLink(item, "id");
                    }
                }
            }
        }
    }
}
