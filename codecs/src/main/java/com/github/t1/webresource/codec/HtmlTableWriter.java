package com.github.t1.webresource.codec;

import java.util.*;

import javax.inject.Inject;

import com.github.t1.webresource.codec.HtmlOut.Tag;
import com.github.t1.webresource.meta.*;

public class HtmlTableWriter {
    @Inject
    HtmlOut out;
    @Inject
    IdGenerator ids;
    @Inject
    HtmlListWriter listWriter;
    @Inject
    HtmlFieldWriter fieldWriter;
    @Inject
    HtmlLinkWriter linkWriter;

    public void write(Item listItem) {
        List<Item> list = listItem.list();
        Collection<Trait> traits = list.get(0).traits();
        try (Tag table = out.tag("table")) {
            try (Tag thead = out.tag("thead")) {
                writeTableHead(traits);
            }
            try (Tag tbody = out.tag("tbody")) {
                for (Item element : list) {
                    writeTableRow(traits, element);
                }
            }
        }
    }

    private void writeTableHead(Collection<Trait> traits) {
        try (Tag tr = out.tag("tr")) {
            for (Trait trait : traits) {
                try (Tag th = out.tag("th")) {
                    out.writeEscapedObject(new FieldName(trait));
                }
            }
        }
    }

    // this duplicates a lot of writeTableHead... closures would be nice, here ;-)
    private void writeTableRow(Collection<Trait> traits, Item rowItem) {
        try (Tag tr = out.tag("tr")) {
            for (Trait trait : traits) {
                try (Tag td = out.tag("td")) {
                    Item cellItem = rowItem.read(trait);
                    String id = ids.get(trait);
                    if (cellItem.isSimple()) {
                        Trait simple = SimpleTrait.of(cellItem);
                        fieldWriter.write(cellItem, simple, id);
                    } else if (cellItem.isList()) {
                        listWriter.write(cellItem);
                    } else {
                        linkWriter.write(cellItem, id);
                    }
                }
            }
        }
    }
}
