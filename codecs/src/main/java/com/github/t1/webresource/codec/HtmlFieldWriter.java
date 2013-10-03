package com.github.t1.webresource.codec;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

import com.github.t1.webresource.meta.*;

@Slf4j
public class HtmlFieldWriter extends AbstractHtmlWriter {
    private final Item item;
    private final Trait trait;
    private final String id;

    public HtmlFieldWriter(AbstractHtmlWriter context, Item item, Trait trait, String id) {
        super(context);
        this.item = item;
        this.trait = trait;
        this.id = id;
    }

    public void write() throws IOException {
        if (item.isSimple()) {
            escaped().append(item.get(trait).toString());
        } else {
            Item fieldItem = item.get(trait);
            if (fieldItem.isList()) {
                writeList(fieldItem);
            } else {
                writeField();
            }
        }
    }

    private void writeField() {
        write("<input");
        if (id != null)
            write(" id='" + id + "'");
        write(" class='" + trait.type() + "'");
        write(" type='" + inputType() + "'");
        write(" value='" + item.get(trait) + "'");
        write("/>\n");
    }

    private String inputType() {
        if (trait.is(HtmlInputType.class))
            return trait.get(HtmlInputType.class).value();
        String itemType = trait.type();
        if (itemType == null)
            return "text";
        switch (itemType) {
            case "boolean":
                return "checkbox";
            case "integer":
            case "string":
                return "text";
            default:
                log.debug("unknown item type [" + itemType + "] for field. Default to 'text'");
                return "text";
        }
    }
}
