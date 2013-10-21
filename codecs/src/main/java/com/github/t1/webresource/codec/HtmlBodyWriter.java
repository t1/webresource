package com.github.t1.webresource.codec;

import java.io.IOException;
import java.util.*;

import com.github.t1.webresource.meta.*;

public class HtmlBodyWriter extends AbstractHtmlWriter {

    private Item item;

    @Override
    public void write(Item item) {
        this.item = item;
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
            writeItemList();
        } else {
            writeForm(item);
        }
    }

    private void writeItemList() {
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
