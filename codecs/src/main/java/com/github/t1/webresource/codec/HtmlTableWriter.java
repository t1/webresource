package com.github.t1.webresource.codec;

import java.io.IOException;
import java.util.*;

import javax.inject.Inject;

import com.github.t1.webresource.meta.*;

public class HtmlTableWriter extends AbstractHtmlWriter {
    @Inject
    IdGenerator ids;

    public void write(Item listItem) {
        List<Item> list = listItem.getList();
        Collection<Trait> traits = list.get(0).traits();
        try (Tag table = new Tag("table")) {
            try (Tag thead = new Tag("thead")) {
                writeTableHead(traits);
            }
            try (Tag tbody = new Tag("tbody")) {
                for (Item element : list) {
                    writeTableRow(traits, element);
                }
            }
        }
    }

    private void writeTableHead(Collection<Trait> traits) {
        try (Tag tr = new Tag("tr")) {
            for (Trait trait : traits) {
                try (Tag th = new Tag("th")) {
                    escaped().append(new FieldName(trait));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    // this duplicates a lot of writeTableHead... closures would be nice, here ;-)
    private void writeTableRow(Collection<Trait> traits, Item rowItem) {
        try (Tag tr = new Tag("tr")) {
            for (Trait trait : traits) {
                try (Tag td = new Tag("td")) {
                    Item cellItem = rowItem.get(trait);
                    String id = ids.get(trait);
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
