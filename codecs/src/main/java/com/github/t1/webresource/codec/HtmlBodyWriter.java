package com.github.t1.webresource.codec;

import java.io.IOException;
import java.util.*;

import javax.inject.Inject;

import com.github.t1.webresource.meta.*;

public class HtmlBodyWriter extends AbstractHtmlWriter {

    @Inject
    HtmlFormWriter htmlFormWriter;

    public void write(Item item) {
        if (item.isNull())
            return;
        nl();
        if (item.isSimple()) {
            try {
                escaped().write(item.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (item.isList()) {
            writeItemList(item);
        } else {
            htmlFormWriter.write(item);
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
            writeList(item);
        } else {
            writeTable(item);
        }
    }
}
