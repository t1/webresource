package com.github.t1.webresource.accessors;

import java.net.URI;

public class UriAccessor extends AbstractAccessor<URI> {
    @Override
    public URI link(URI element) {
        return element;
    }
}
