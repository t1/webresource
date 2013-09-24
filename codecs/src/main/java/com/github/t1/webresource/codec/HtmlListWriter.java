package com.github.t1.webresource.codec;

import java.io.IOException;
import java.util.List;

import com.github.t1.webresource.meta.*;

public class HtmlListWriter extends AbstractHtmlWriter {

    private final List<Item> list;

    public HtmlListWriter(AbstractHtmlWriter context, List<Item> list) {
        super(context);
        this.list = list;
    }

    public void write() throws IOException {
        try (Tag ul = new Tag("ul")) {
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
