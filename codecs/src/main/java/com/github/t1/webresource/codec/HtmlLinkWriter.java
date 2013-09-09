package com.github.t1.webresource.codec;

import java.io.IOException;
import java.lang.reflect.Field;

import javax.persistence.Id;

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
        return "../" + type() + "/" + idTraitValue() + ".html";
    }

    private String idTraitValue() {
        Trait idTrait = idTrait();
        if (idTrait == null)
            return String.valueOf(item.target());
        return String.valueOf(item.get(idTrait));
    }

    private Trait idTrait() {
        for (Field field : item.target().getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                return new PojoFieldTrait(field);
            }
        }
        return null;
    }

    private String type() {
        return item.target().getClass().getSimpleName().toLowerCase() + "s";
    }

    private Attribute idAttribute() {
        return new Attribute("id", id + "-href");
    }

    private Attribute classAttribute() {
        return new Attribute("class", type());
    }

    private Object body() {
        Trait trait = item.trait(HtmlLinkValue.class);
        if (trait == null)
            return item.target(); // -> fall back to toString
        return item.get(trait);
    }
}
