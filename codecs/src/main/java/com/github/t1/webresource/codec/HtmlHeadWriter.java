package com.github.t1.webresource.codec;

import java.io.*;
import java.net.*;

import javax.inject.Inject;

import com.github.t1.webresource.meta.*;

public class HtmlHeadWriter extends AbstractHtmlWriter {

    @Inject
    UriResolver uriResolver;

    public void write(Item item) {
        if (!item.isSimple()) {
            writeTitle(item);
            writeStyleSheets(item);
        }
    }

    private void writeTitle(Item item) {
        String titleString = titleString(item);
        if (!titleString.isEmpty()) {
            try (Tag title = new Tag("title")) {
                try {
                    escaped().append(titleString);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private String titleString(Item item) {
        StringWriter titleString = new StringWriter();
        Delimiter delim = new Delimiter(titleString, " - ");
        for (Trait trait : item.traits()) {
            if (trait.is(HtmlHead.class)) {
                delim.write();
                titleString.append(item.get(trait).toString());
            }
        }
        return titleString.toString();
    }

    private void writeStyleSheets(Item item) {
        if (item.is(HtmlStyleSheet.class)) {
            nl();
            writeStyleSheet(item.get(HtmlStyleSheet.class));
        }
        if (item.is(HtmlStyleSheets.class)) {
            nl();
            for (HtmlStyleSheet styleSheet : item.get(HtmlStyleSheets.class).value()) {
                writeStyleSheet(styleSheet);
            }
        }
    }

    private void writeStyleSheet(HtmlStyleSheet styleSheet) {
        URI uri = URI.create(styleSheet.value());
        if (styleSheet.inline()) {
            try (Tag style = new Tag("style")) {
                nl();
                writeResource(uri);
            }
        } else {
            if (!isRootPath(uri))
                uri = insertApplicationPath(uri);
            write("<link rel='stylesheet' href='" + uri + "' type='text/css'/>\n");
        }
    }

    private void writeResource(URI uri) {
        uri = uriResolver.resolveApp(uri);
        try (InputStream inputStream = uri.toURL().openStream()) {
            write(inputStream);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isRootPath(URI uri) {
        return uri.isAbsolute() || uri.getPath() == null || uri.getPath().startsWith("/");
    }

    private URI insertApplicationPath(URI uri) {
        return uri.resolve("/" + uriResolver.applicationPath().resolve(uri.getPath()));
    }
}
