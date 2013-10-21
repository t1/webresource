package com.github.t1.webresource.codec;

import java.net.URI;
import java.nio.file.*;

import javax.ws.rs.core.UriInfo;

public class UriResolver {

    private final UriInfo uriInfo;

    public UriResolver(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    /**
     * The path of the JAX-RS base-uri contains the resource base (often 'rest'), but we need the application base,
     * which is the first path element.
     */
    public Path applicationPath() {
        String path = uriInfo.getBaseUri().getPath();
        return Paths.get(path).getName(0);
    }

    /**
     * Resolve the given URI against the application base, i.e.
     * <ul>
     * <li>if the given uri is absolut (contains a protocol like http), use that
     * <li>if the given uri path starts with a slash, use the same host, but nothing from the application path, or
     * <li>if the given uri does not start with a slash, resolve within the application.
     * </ul>
     * 
     * @see HtmlStyleSheet
     */
    public URI resolveApp(URI uri) {
        if (uri.isAbsolute())
            return uri;
        if (uri.getPath() == null)
            throw new IllegalArgumentException("the given uri has no path: " + uri);
        if (uri.getPath().startsWith("/")) {
            return uriInfo.getBaseUri().resolve(uri.getPath());
        } else {
            Path path = Paths.get(uriInfo.getBaseUri().getPath()).subpath(0, 1).resolve(uri.getPath());
            return uriInfo.getBaseUri().resolve("/" + path);
        }
    }

    /**
     * Resolve the given path against the base uri (i.e. including the application and the 'rest' or 'resource' path
     * elements)
     */
    public URI resolveBase(String path) {
        return URI.create(uriInfo.getBaseUri() + path);
    }
}
