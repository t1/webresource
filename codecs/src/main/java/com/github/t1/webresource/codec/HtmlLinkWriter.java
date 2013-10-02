package com.github.t1.webresource.codec;

import java.io.IOException;
import java.util.regex.*;

import lombok.extern.slf4j.Slf4j;

import com.github.t1.webresource.WebResourceKey;
import com.github.t1.webresource.meta.*;

// TODO escape strings?
@Slf4j
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
        if (item.is(HtmlLinkText.class)) {
            HtmlLinkText linkText = item.get(HtmlLinkText.class);
            log.debug("found link text {} on type {}", linkText, item);
            return resolveVariables(linkText.value());
        }

        Trait linkTextTrait = item.trait(HtmlLinkText.class);
        if (linkTextTrait != null) {
            log.debug("found link text trait {}", linkTextTrait);
            return item.get(linkTextTrait).toString();
        }

        Trait webResourceKeyTrait = item.trait(WebResourceKey.class);
        if (webResourceKeyTrait != null) {
            log.debug("found webresource key trait {}", webResourceKeyTrait);
            return item.get(webResourceKeyTrait).toString();
        }

        log.debug("found no text link annotations on {}; fall back to toString", item);
        return item.toString();
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
