package com.github.t1.webresource.accessors;

import java.util.Map;

public class MapMetaData implements MetaData<Map<?, ?>> {
    private final String title;
    private final String keyTitle;
    private final String valueTitle;

    public MapMetaData(String pageTitle, String keyTitle, String valueTitle) {
        this.title = pageTitle;
        this.keyTitle = keyTitle;
        this.valueTitle = valueTitle;
    }

    @Override
    public String title() {
        return title;
    }

    public String keyTitle() {
        return keyTitle;
    }

    public String valueTitle() {
        return valueTitle;
    }
}
