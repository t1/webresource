package com.github.t1.webresource.codec;

import java.io.IOException;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import com.github.t1.webresource.meta.*;

@Slf4j
public class HtmlFieldWriter {
    @Inject
    HtmlOut out;

    public void write(Item item, Trait trait, String id) {
        if (item.isSimple()) {
            try {
                out.escaped().append(item.get(trait).toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            Item fieldItem = item.get(trait);
            if (fieldItem.isList()) {
                out.writeList(fieldItem);
            } else {
                writeInput(item, trait, id);
            }
        }
    }

    private void writeInput(Item item, Trait trait, String id) {
        out.write("<input");
        if (id != null)
            out.write(" id='" + id + "'");
        out.write(" name='" + trait.name() + "'");
        out.write(" class='" + trait.type() + "'");
        out.write(" type='" + inputType(trait) + "'");
        out.write(" value='" + item.get(trait) + "'");
        out.write("/>\n");
    }

    private String inputType(Trait trait) {
        if (trait.is(HtmlInputType.class))
            return trait.get(HtmlInputType.class).value();
        String itemType = trait.type();
        if (itemType == null)
            return "text";
        switch (itemType) {
            case "boolean":
                return "checkbox";
            case "number":
            case "integer":
            case "string":
                return "text";
            default:
                log.debug("unknown item type [" + itemType + "] for field. Default to 'text'");
                return "text";
        }
    }
}
