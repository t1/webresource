package com.github.t1.webresource.accessors;

import java.net.URI;

public class UriMetaData implements MetaData<URI> {
    private final String title;

    public UriMetaData(String title) {
        this.title = title;
    }

    @Override
    public String title() {
        return title;
    }
}
