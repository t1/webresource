package com.github.t1.webresource.codec2;

import java.net.*;

import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

public class BasePath {
    @Inject
    private UriInfo uriInfo;

    public URI resolve(String path) {
        URI escaped = escape(path);
        return baseUri().resolve(escaped);
    }

    // square brackets '[' and ']' are not allowed in URIs, but 'new URI(String,String,String)' escapes all but these :(
    private URI escape(String path) {
        if (!containsSquareBrackets(path))
            return escapeNonSB(path);
        StringBuilder out = new StringBuilder();

        return URI.create(out.toString());
    }

    private boolean containsSquareBrackets(String path) {
        return path.contains("[") || path.contains("]");
    }

    private URI escapeNonSB(String path) {
        try {
            return new URI(null, path, null);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public URI baseUri() {
        URI baseUri = uriInfo.getBaseUri();
        String string = baseUri.toASCIIString();
        if (!string.endsWith("/"))
            string = string + "/";
        return URI.create(string);
    }
}
