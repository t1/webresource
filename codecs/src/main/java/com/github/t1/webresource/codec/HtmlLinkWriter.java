package com.github.t1.webresource.codec;

import java.io.IOException;
import java.util.regex.*;

import com.github.t1.webresource.meta.*;

// TODO escape strings
public class HtmlLinkWriter extends AbstractHtmlWriter {

    private static final Pattern VAR = Pattern.compile("\\$\\{([^}]*)\\}");

    private final Item item;
    private final String id;

    public HtmlLinkWriter(AbstractHtmlWriter context, Item item, String id) {
        super(context);
        this.item = item;
        this.id = id;
    }

    public void write() throws IOException {
        try (Tag a = new Tag("a", hrefAttribute(), idAttribute(), classAttribute())) {
            write(body());
        }
    }

    private Attribute hrefAttribute() {
        return new Attribute("href", href());
    }

    private String href() {
        return resolveBase(item.type() + "/" + idTraitValue() + ".html").toString();
    }

    private String idTraitValue() {
        Trait idTrait = item.id();
        if (idTrait == null)
            return item.toString();
        return item.get(idTrait).toString();
    }

    private Attribute idAttribute() {
        return new Attribute("id", id + "-href");
    }

    private Attribute classAttribute() {
        return new Attribute("class", item.type());
    }

    private Object body() {
        if (item.is(HtmlLinkText.class))
            return resolveVariables(item.get(HtmlLinkText.class).value());
        Trait trait = item.trait(HtmlLinkText.class);
        if (trait == null)
            return item.toString(); // -> fall back
        return item.get(trait).toString();
    }

    private String resolveVariables(String text) {
        Matcher matcher = VAR.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String name = matcher.group(1);
            Trait trait = item.trait(name);
            String value = item.get(trait).toString();
            matcher.appendReplacement(sb, value);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
