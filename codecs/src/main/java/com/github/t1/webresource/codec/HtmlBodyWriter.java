package com.github.t1.webresource.codec;

import java.util.*;

import javax.inject.Inject;

import com.github.t1.webresource.meta.*;

public class HtmlBodyWriter {
    @Inject
    HtmlOut out;
    @Inject
    HtmlFormWriter formWriter;
    @Inject
    HtmlListWriter listWriter;
    @Inject
    HtmlTableWriter tableWriter;
    @Inject
    HtmlLinkWriter linkWriter;

    public void write(Item item) {
        if (item.isNull())
            return;
        out.nl();
        if (item.isType()) {
            linkWriter.write(item, null);
        } else if (item.isSimple()) {
            out.writeEscapedObject(item);
        } else if (item.isList()) {
            writeItemList(item);
        } else {
            formWriter.write(item);
        }
    }

    private void writeItemList(Item item) {
        List<Item> list = item.getList();
        if (list.isEmpty())
            return;
        Collection<Trait> traits = list.get(0).traits();
        if (traits.isEmpty())
            return;
        if (traits.size() == 1 && traits.iterator().next() instanceof SimpleTrait) {
            listWriter.write(item);
        } else {
            tableWriter.write(item);
        }
    }
}
