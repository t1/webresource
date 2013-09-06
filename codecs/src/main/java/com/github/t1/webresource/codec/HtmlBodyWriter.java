package com.github.t1.webresource.codec;

import static com.github.t1.webresource.meta.SimpleTrait.*;

import java.io.IOException;
import java.util.*;

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
        if (item.isSimple()) {
            escaped().write(Objects.toString(item.get(SIMPLE)));
        } else if (item.isList()) {
            writeItemList();
        } else {
            writeForm(item);
        }
    }

    private void writeItemList() throws IOException {
        List<Item> list = item.getList();
        if (list.isEmpty())
            return;
        List<Trait> traits = list.get(0).traits();
        if (traits.isEmpty())
            return;
        if (traits.size() == 1 && SIMPLE == traits.get(0)) {
            writeList(list, traits.get(0));
        } else {
            writeTable(list, traits);
        }
    }
}
