package com.github.t1.webresource.codec;

import java.util.List;

import com.github.t1.webresource.meta.*;

public class HtmlListWriter extends AbstractHtmlWriter {
    private String type;
    private List<Item> list;

    @Override
    public void write(Item listItem) {
        this.list = listItem.getList();
        this.type = list.isEmpty() ? "empty" : list.get(0).type();

        try (Tag ul = new Tag("ul", new Attribute("class", type))) {
            for (Item item : list) {
                try (Tag li = new Tag("li")) {
                    if (item.isSimple()) {
                        write(item.get(SimpleTrait.of(item)).toString());
                    } else {
                        writeLink(item, "id");
                    }
                }
            }
        }
    }
}
