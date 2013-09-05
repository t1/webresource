package com.github.t1.webresource.codec;

import java.io.Writer;
import java.net.URI;
import java.nio.file.*;

/**
 * The context to pass around {@link AbstractHtmlWriter}s. It's a writer and holds the base uri to resolve things
 * against.
 */
public class HtmlWriter extends CodePointFilterWriter {
    private final URI baseUri;

    public HtmlWriter(Writer out, URI baseUri) {
        super(out);
        this.baseUri = baseUri;
    }

    /**
     * The path of the JAX-RS base-uri starts with the resource base (often 'rest'), but we need the application base,
     * which is the first path element.
     */
    public Path applicationPath() {
        return Paths.get(baseUri.getPath()).getName(0);
    }

    /**
     * Resolve the given URI.
     * 
     * @see HtmlStyleSheet
     */
    public URI resolve(URI uri) {
        if (uri.isAbsolute())
            return uri;
        if (uri.getPath() == null)
            throw new IllegalArgumentException("the given uri has no path: " + uri);
        if (uri.getPath().startsWith("/")) {
            return baseUri.resolve(uri.getPath());
        } else {
            Path path = Paths.get(baseUri.getPath()).subpath(0, 1).resolve(uri.getPath());
            return baseUri.resolve("/" + path);
        }
    }
}
