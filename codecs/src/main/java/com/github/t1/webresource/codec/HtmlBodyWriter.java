package com.github.t1.webresource.codec;

import java.io.IOException;
import java.util.List;

import com.github.t1.webresource.meta.*;

public class HtmlBodyWriter extends AbstractHtmlWriter {

    private final Item item;

    public HtmlBodyWriter(AbstractHtmlWriter context, Item item) {
        super(context);
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
        if (traits.isEmpty())
            return;
        if (traits.size() == 1 && SimpleTrait.SIMPLE == traits.get(0)) {
            writeList(list, traits.get(0));
        } else {
            writeTable(list, traits);
        }
    }

    private void writeMap() throws IOException {
        List<Trait> traits = item.traits();
        switch (traits.size()) {
            case 0:
                break;
            case 1:
                writeField(item, traits.get(0), null);
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
                writeField(item, trait, id);
            }
        }
    }
}
