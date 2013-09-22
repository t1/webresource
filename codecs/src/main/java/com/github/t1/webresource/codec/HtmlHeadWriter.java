package com.github.t1.webresource.codec;

import java.io.*;
import java.net.URI;

import com.github.t1.webresource.meta.*;

public class HtmlHeadWriter extends AbstractHtmlWriter {

    private final Item item;

    public HtmlHeadWriter(AbstractHtmlWriter context, Item item) {
        super(context);
        this.item = item;
    }

    public void write() throws IOException {
        if (!item.isSimple()) {
            writeTitle();
            writeStyleSheets();
        }
    }

    private void writeTitle() throws IOException {
        String titleString = titleString();
        if (!titleString.isEmpty()) {
            try (Tag title = new Tag("title")) {
                escaped().append(titleString);
            }
        }
    }

    private String titleString() throws IOException {
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

    private void writeStyleSheets() throws IOException {
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

    private void writeStyleSheet(HtmlStyleSheet styleSheet) throws IOException {
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

    private void writeResource(URI uri) throws IOException {
        uri = resolveApp(uri);
        try (InputStream inputStream = uri.toURL().openStream()) {
            write(inputStream);
        }
    }

    private boolean isRootPath(URI uri) {
        return uri.isAbsolute() || uri.getPath() == null || uri.getPath().startsWith("/");
    }

    private URI insertApplicationPath(URI uri) {
        return uri.resolve("/" + applicationPath().resolve(uri.getPath()));
    }
}
