package com.github.t1.webresource.meta2;

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
