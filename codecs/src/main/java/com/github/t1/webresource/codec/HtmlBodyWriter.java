package com.github.t1.webresource.codec;

import java.io.IOException;
import java.util.*;

import com.github.t1.webresource.meta.*;

public class HtmlBodyWriter extends AbstractHtmlWriter {

    private final Map<String, Integer> ids = new HashMap<>();
    private final Item item;

    public HtmlBodyWriter(HtmlWriter out, Item item) {
        super(out);
        this.item = item;
    }

    public void write() throws IOException {
        if (item.isNull())
            return;
        nl();
        if (item.isList()) {
            writeItemList();
        } else {
            writeMap();
        }
    }

    private void writeItemList() throws IOException {
        List<Item> list = item.getList();
        if (list.isEmpty())
            return;
        List<Trait> traits = list.get(0).traits();
        switch (traits.size()) {
            case 0:
                break;
            case 1:
                new HtmlListWriter(out, list, traits.get(0)).write();
                break;
            default:
                new HtmlTableWriter(out, list, traits).write();
        }
    }

    private void writeMap() throws IOException {
        List<Trait> traits = item.traits();
        switch (traits.size()) {
            case 0:
                break;
            case 1:
                new HtmlFieldWriter(out, item, traits.get(0), null).write();
                break;
            default:
                writeTraits(traits);
        }
    }

    private void writeTraits(List<Trait> traits) throws IOException {
        for (Trait trait : traits) {
            try (Tag div = new Tag("div")) {
                String name = trait.getName();
                String id = id(name);
                try (Tag label = new Tag("label", new Attribute("for", id), new Attribute("class", name + "-label"))) {
                    escaped().write(name);
                }
                new HtmlFieldWriter(out, item, trait, id).write();
            }
        }
    }

    private String id(String name) {
        Integer i = ids.get(name);
        if (i == null)
            i = 0;
        ids.put(name, i + 1);
        return name + "-" + i;
    }
}
