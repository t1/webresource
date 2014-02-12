package com.github.t1.webresource.meta2;

import java.util.Map;

public class MapMetaData implements MetaData<Map<?, ?>> {
    private final String pageTitle;
    private final String keyTitle;
    private final String valueTitle;

    public MapMetaData(String pageTitle, String keyTitle, String valueTitle) {
        this.pageTitle = pageTitle;
        this.keyTitle = keyTitle;
        this.valueTitle = valueTitle;
    }

    public String pageTitle() {
        return pageTitle;
    }

    public String keyTitle() {
        return keyTitle;
    }

    public String valueTitle() {
        return valueTitle;
    }
}
