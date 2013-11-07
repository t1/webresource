package com.github.t1.webresource.codec;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import com.github.t1.webresource.meta.*;

@Slf4j
public class HtmlFieldWriter {
    @Inject
    HtmlOut out;
    @Inject
    HtmlListWriter listWriter;

    public void write(Item item, Trait trait, String id) {
        if (item.isSimple()) {
            out.writeEscapedObject(item.read(trait));
        } else {
            Item fieldItem = item.read(trait);
            if (fieldItem.isList()) {
                listWriter.write(fieldItem);
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
        out.write(" value='" + item.read(trait) + "'");
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
