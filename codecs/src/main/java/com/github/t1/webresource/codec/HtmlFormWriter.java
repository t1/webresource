package com.github.t1.webresource.codec;

import java.io.IOException;

import javax.inject.Inject;

import com.github.t1.webresource.meta.*;

public class HtmlFormWriter extends AbstractHtmlWriter {

    @Inject
    UriResolver uriResolver;

    @Inject
    IdGenerator ids;

    public void write(Item item) {
        try (Tag form = new Tag("form", //
                new IdAttribute(item, "form"), //
                new ActionAttribute(uriResolver, item), //
                new Attribute("method", "post") //
                )) {
            Trait idTrait = HtmlId.of(item).trait();
            if (idTrait != null) {
                nl();
                write("<input name=\"" + idTrait.name() + "\" type=\"hidden\" value=\"" + item.get(idTrait) + "\"/>");
            }
            nl();
            for (Trait trait : item.traits()) {
                writeFormDiv(item, trait);
            }
            write("<input type=\"submit\" value=\"submit\"/>");
            nl();
        }
    }

    private void writeFormDiv(Item item, Trait trait) {
        String id = ids.get(trait);
        try (Tag div = new Tag("div" /* TODO , new Attribute("class", name + "-item") */)) {
            try (Tag label = new Tag("label", new Attribute("for", id), new ClassAttribute(trait, "label"))) {
                escaped().append(new FieldName(trait));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            writeItem(item, trait, id);
        }
    }

    private void writeItem(Item item, Trait trait, String id) {
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
