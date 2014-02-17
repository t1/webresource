package com.github.t1.webresource.accessors;

import java.util.List;

public class ListMetaData implements MetaData<List<?>> {
    private final String title;

    public ListMetaData(String pageTitle) {
        this.title = pageTitle;
    }

    @Override
    public String title() {
        return title;
    }
}
