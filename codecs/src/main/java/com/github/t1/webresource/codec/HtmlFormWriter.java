package com.github.t1.webresource.codec;

import java.io.IOException;

import com.github.t1.webresource.meta.*;

public class HtmlFormWriter extends AbstractHtmlWriter {

    private final Item item;

    public HtmlFormWriter(AbstractHtmlWriter context, Item item) {
        super(context);
        this.item = item;
    }

    public void write() throws IOException {
        try (Tag form = new Tag("form", //
                new Attribute("id", item.type() + "-form"), //
                new Attribute("action", resolveBase(item.type()).toString()), //
                new Attribute("method", "post") //
                )) {
            Trait idTrait = HtmlId.of(item).trait();
            if (idTrait != null) {
                nl();
                write("<input name=\"" + idTrait.name() + "\" type=\"hidden\" value=\"" + item.get(idTrait) + "\"/>");
            }
            nl();
            for (Trait trait : item.traits()) {
                writeFormDiv(trait);
            }
            write("<input type=\"submit\" value=\"submit\"/>");
            nl();
        }
    }

    private void writeFormDiv(Trait trait) throws IOException {
        String name = name(trait);
        String id = id(name);
        try (Tag div = new Tag("div" /* TODO , new Attribute("class", name + "-item") */)) {
            try (Tag label = new Tag("label", new Attribute("for", id), new Attribute("class", name + "-label"))) {
                escaped().write(name);
            }
            writeItem(trait, id);
        }
    }

    private void writeItem(Trait trait, String id) throws IOException {
        Item value = item.get(trait);
        if (value.isSimple()) {
            writeField(item, trait, id);
        } else if (value.isList()) {
            writeList(value);
        } else {
            writeLink(value, id);
        }
    }
}
