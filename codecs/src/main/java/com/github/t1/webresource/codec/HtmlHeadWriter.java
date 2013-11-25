package com.github.t1.webresource.codec;

import java.io.*;
import java.lang.annotation.Annotation;
import java.net.*;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import com.github.t1.log.Logged;
import com.github.t1.webresource.codec.HtmlOut.Tag;
import com.github.t1.webresource.meta.*;

@Slf4j
public class HtmlHeadWriter {

    @Inject
    HtmlOut out;
    @Inject
    UriResolver uriResolver;
    @Inject
    HtmlTitleWriter titleWriter;

    @Logged
    public void write(Item item) {
        if (item.isSimple()) {
            log.debug("no header for simple item {}", item);
        } else {
            writeTitle(item);
            writeStyleSheets(item);
        }
    }

    private void writeTitle(Item item) {
        String titleString = titleWriter.title(item);
        if (!titleString.isEmpty()) {
            try (Tag title = out.tag("title")) {
                out.writeEscaped(titleString);
            }
        }
    }

    private void writeStyleSheets(Item item) {
        log.debug("{} is annotated as:", item);
        for (Annotation annotation : ((AbstractItem) item).annotations().getDeclaredAnnotations()) {
            log.debug("   {}", annotation);
        }
        if (item.is(HtmlStyleSheet.class)) {
            out.nl();
            writeStyleSheet(item.get(HtmlStyleSheet.class));
        }
        if (item.is(HtmlStyleSheets.class)) {
            out.nl();
            for (HtmlStyleSheet styleSheet : item.get(HtmlStyleSheets.class).value()) {
                writeStyleSheet(styleSheet);
            }
        }
    }

    private void writeStyleSheet(HtmlStyleSheet styleSheet) {
        URI uri = URI.create(styleSheet.value());
        if (styleSheet.inline()) {
            try (Tag style = out.tag("style")) {
                out.nl();
                writeResource(uri);
            }
        } else {
            if (!isRootPath(uri))
                uri = insertApplicationPath(uri);
            out.write("<link rel='stylesheet' href='" + uri + "' type='text/css'/>\n");
        }
    }

    private void writeResource(URI uri) {
        uri = uriResolver.resolveApp(uri);
        try (InputStream inputStream = uri.toURL().openStream()) {
            out.write(inputStream);
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
