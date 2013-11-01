package com.github.t1.webresource.codec;

import java.util.List;
import java.util.regex.*;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import com.github.t1.webresource.codec.HtmlOut.Attribute;
import com.github.t1.webresource.codec.HtmlOut.Tag;
import com.github.t1.webresource.meta.*;

// TODO escape strings?
@Slf4j
public class HtmlLinkWriter {
    private static final Pattern VAR = Pattern.compile("\\$\\{([^}]*)\\}");

    @Inject
    HtmlOut out;
    @Inject
    UriResolver uriResolver;

    public void write(Item item, String id) {
        try (Tag a = out.tag("a", new HrefAttribute(uriResolver, item), idAttribute(id), new ClassAttribute(item))) {
            out.write(body(item));
        }
    }

    private Attribute idAttribute(String id) {
        return new Attribute("id", id + "-href");
    }

    private String body(Item item) {
        if (item.is(HtmlLinkText.class)) {
            HtmlLinkText linkText = item.get(HtmlLinkText.class);
            log.debug("found link text {} on type {}", linkText, item);
            return resolveVariables(linkText.value(), item);
        }

        List<Trait> linkTextTraits = item.trait(HtmlLinkText.class);
        if (!linkTextTraits.isEmpty()) {
            if (linkTextTraits.size() > 1)
                throw new RuntimeException("found more than one HtmlLinkText traits: " + linkTextTraits);
            log.debug("found link text trait {}", linkTextTraits);
            return item.get(linkTextTraits.get(0)).toString();
        }

        Trait webResourceKeyTrait = HtmlId.of(item).getWebResourceKey();
        if (webResourceKeyTrait != null)
            return item.get(webResourceKeyTrait).toString();

        log.debug("found no text link annotations on {}; fall back to toString", item);
        return item.toString();
    }

    private String resolveVariables(String text, Item item) {
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
