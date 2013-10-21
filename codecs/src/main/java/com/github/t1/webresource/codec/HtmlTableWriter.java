package com.github.t1.webresource.codec;

import java.io.IOException;
import java.util.*;

import com.github.t1.webresource.meta.*;

public class HtmlTableWriter extends AbstractHtmlWriter {
    private List<Item> list;
    private Collection<Trait> traits;

    @Override
    public void write(Item listItem) {
        this.list = listItem.getList();
        this.traits = list.get(0).traits();
        try (Tag table = new Tag("table")) {
            try (Tag thead = new Tag("thead")) {
                writeTableHead();
            }
            try (Tag tbody = new Tag("tbody")) {
                for (Item element : list) {
                    writeTableRow(element);
                }
            }
        }
    }

    private void writeTableHead() {
        try (Tag tr = new Tag("tr")) {
            for (Trait trait : traits) {
                try (Tag th = new Tag("th")) {
                    escaped().append(name(trait));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    // this duplicates a lot of writeTableHead... closures would be nice, here ;-)
    private void writeTableRow(Item rowItem) {
        try (Tag tr = new Tag("tr")) {
            for (Trait trait : traits) {
                try (Tag td = new Tag("td")) {
                    Item cellItem = rowItem.get(trait);
                    String id = id(name(trait));
                    if (cellItem.isSimple()) {
                        Trait simple = SimpleTrait.of(cellItem);
                        writeField(cellItem, simple, id);
                    } else if (cellItem.isList()) {
                        writeList(cellItem);
                    } else {
                        writeLink(cellItem, id);
                    }
                }
            }
        }
    }
}
