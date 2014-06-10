package com.github.t1.webresource.codec2;

import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

public class BasePath {
    @Inject
    private UriInfo uriInfo;
    @Inject
    private UriEscaper escaper;

    public URI resolve(String path) {
        URI escaped = escaper.escape(path);
        return resolve(escaped);
    }

    public URI resolve(URI uri) {
        return baseUri().resolve(uri);
    }

    public URI baseUri() {
        URI baseUri = uriInfo.getBaseUri();
        if (baseUri == null) // TODO this is not for real
            baseUri = URI.create("http://some.example.com/");
        String string = baseUri.toASCIIString();
        if (!string.endsWith("/"))
            string = string + "/";
        return URI.create(string);
    }
}
