package com.github.t1.webresource.codec;

import java.io.StringWriter;
import java.util.List;
import java.util.regex.*;

import lombok.extern.slf4j.Slf4j;

import com.github.t1.webresource.meta.*;

// TODO escape strings?
@Slf4j
public class HtmlTitleWriter {
    private static final Pattern VAR = Pattern.compile("\\$\\{([^}]*)\\}");

    public String title(Item item) {
        if (item.is(HtmlTitle.class)) {
            HtmlTitle htmlTitle = item.get(HtmlTitle.class);
            log.debug("found link text {} on type {}", htmlTitle, item);
            return resolveVariables(htmlTitle.value(), item);
        }

        List<Trait> htmlTitleTraits = item.trait(HtmlTitle.class);
        if (!htmlTitleTraits.isEmpty()) {
            log.debug("found link text trait {}", htmlTitleTraits);
            StringWriter titleString = new StringWriter();
            Delimiter delim = new Delimiter(titleString, " ");
            for (Trait trait : htmlTitleTraits) {
                delim.write();
                titleString.append(item.get(trait).toString());
            }
            return titleString.toString();
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
