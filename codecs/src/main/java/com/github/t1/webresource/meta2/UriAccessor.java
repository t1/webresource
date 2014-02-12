package com.github.t1.webresource.meta2;

import java.net.URI;

import javax.inject.Inject;

public class UriAccessor implements Accessor<URI> {
    @Inject
    MetaDataStore metaDataStore;

    @Override
    public String title(URI element) {
        UriMetaData meta = metaDataStore.get(element);
        return (meta == null) ? "link" : meta.title();
    }

    @Override
    public URI link(URI element) {
        return element;
    }
}
