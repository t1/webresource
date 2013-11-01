package com.github.t1.webresource.codec;

import java.io.IOException;

import javax.inject.Inject;

import com.github.t1.webresource.codec.HtmlOut.Attribute;
import com.github.t1.webresource.codec.HtmlOut.Tag;
import com.github.t1.webresource.meta.*;

public class HtmlFormWriter {

    @Inject
    HtmlOut out;
    @Inject
    UriResolver uriResolver;
    @Inject
    IdGenerator ids;

    public void write(Item item) {
        try (Tag form = out.tag("form", //
                new IdAttribute(item, "form"), //
                new ActionAttribute(uriResolver, item), //
                new Attribute("method", "post") //
                )) {
            Trait idTrait = HtmlId.of(item).trait();
            if (idTrait != null) {
                out.nl();
                out.write("<input name=\"" + idTrait.name() + "\" type=\"hidden\" value=\"" + item.get(idTrait)
                        + "\"/>");
            }
            out.nl();
            for (Trait trait : item.traits()) {
                writeFormDiv(item, trait);
            }
            out.write("<input type=\"submit\" value=\"submit\"/>");
            out.nl();
        }
    }

    private void writeFormDiv(Item item, Trait trait) {
        String id = ids.get(trait);
        try (Tag div = out.tag("div" /* TODO , new Attribute("class", name + "-item") */)) {
            try (Tag label = out.tag("label", new Attribute("for", id), new ClassAttribute(trait, "label"))) {
                out.escaped().append(new FieldName(trait));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            writeItem(item, trait, id);
        }
    }

    private void writeItem(Item item, Trait trait, String id) {
        Item value = item.get(trait);
        if (value.isSimple()) {
            out.writeField(item, trait, id);
        } else if (value.isList()) {
            out.writeList(value);
        } else {
            out.writeLink(value, id);
        }
    }
}
