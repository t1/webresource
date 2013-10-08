package com.github.t1.webresource.codec;

import java.io.IOException;
import java.util.List;
import java.util.regex.*;

import lombok.extern.slf4j.Slf4j;

import com.github.t1.webresource.meta.*;

// TODO escape strings?
@Slf4j
public class HtmlLinkWriter extends AbstractHtmlWriter {
    private static final Pattern VAR = Pattern.compile("\\$\\{([^}]*)\\}");

    private final Item item;
    private final String linkId;
    private final HtmlId refId;

    public HtmlLinkWriter(AbstractHtmlWriter context, Item item, String linkId) {
        super(context);
        this.item = item;
        this.linkId = linkId;
        this.refId = HtmlId.of(item);
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
        return resolveBase(item.type() + "/" + refId + ".html").toString();
    }

    private Attribute idAttribute() {
        return new Attribute("id", linkId + "-href");
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

        List<Trait> linkTextTraits = item.trait(HtmlLinkText.class);
        if (!linkTextTraits.isEmpty()) {
            if (linkTextTraits.size() > 1)
                throw new RuntimeException("found more than one HtmlLinkText traits: " + linkTextTraits);
            log.debug("found link text trait {}", linkTextTraits);
            return item.get(linkTextTraits.get(0)).toString();
        }

        Trait webResourceKeyTrait = refId.getWebResourceKey();
        if (webResourceKeyTrait != null)
            return item.get(webResourceKeyTrait).toString();

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
