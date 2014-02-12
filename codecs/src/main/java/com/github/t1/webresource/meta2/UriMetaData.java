package com.github.t1.webresource.meta2;

import java.net.URI;

public class UriMetaData implements MetaData<URI> {
    private final String title;

    public UriMetaData(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }
}
