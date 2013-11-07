package com.github.t1.webresource.codec;

import java.util.List;

import javax.inject.Inject;

import com.github.t1.webresource.codec.HtmlOut.Attribute;
import com.github.t1.webresource.codec.HtmlOut.Tag;
import com.github.t1.webresource.meta.*;

public class HtmlListWriter {
    @Inject
    HtmlOut out;
    @Inject
    HtmlLinkWriter linkWriter;

    public void write(Item listItem) {
        List<Item> list = listItem.list();
        String type = list.isEmpty() ? "empty" : list.get(0).type();

        try (Tag ul = out.tag("ul", new Attribute("class", type))) {
            for (Item item : list) {
                try (Tag li = out.tag("li")) {
                    if (item.isSimple()) {
                        out.write(item.read(SimpleTrait.of(item)).toString());
                    } else {
                        linkWriter.write(item, "id");
                    }
                }
            }
        }
    }
}
