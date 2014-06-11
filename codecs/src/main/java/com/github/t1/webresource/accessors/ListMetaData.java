package com.github.t1.webresource.accessors;

import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ListMetaData implements MetaData<List<?>> {
    private final String title;

    @Override
    public String title() {
        return title;
    }
}
