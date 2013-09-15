package com.github.t1.webresource.codec;

import java.io.IOException;

import com.github.t1.webresource.meta.*;

public class HtmlLinkWriter extends AbstractHtmlWriter {

    private final Item item;
    private final String id;

    public HtmlLinkWriter(AbstractHtmlWriter context, Item item, String id) {
        super(context);
        this.item = item;
        this.id = id;
    }

    public void write() throws IOException {
        try (Tag a = new Tag("a", hrefAttribute(), idAttribute(), classAttribute())) {
            append(String.valueOf(body()));
        }
    }

    private Attribute hrefAttribute() {
        return new Attribute("href", href());
    }

    private String href() {
        return "../" + item.type() + "/" + idTraitValue() + ".html";
    }

    private String idTraitValue() {
        Trait idTrait = item.id();
        if (idTrait == null)
            return item.toString();
        return String.valueOf(item.get(idTrait));
    }

    private Attribute idAttribute() {
        return new Attribute("id", id + "-href");
    }

    private Attribute classAttribute() {
        return new Attribute("class", item.type());
    }

    private Object body() {
        Trait trait = item.trait(HtmlLinkText.class);
        if (trait == null)
            return item.toString(); // -> fall back
        return item.get(trait);
    }
}
