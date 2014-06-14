package com.github.t1.webresource.accessors;

import java.net.URI;
import java.util.Map;

public class MapAccessor extends AbstractAccessor<Map<?, ?>> {
    @Override
    public URI link(Map<?, ?> element) {
        return null; // TODO put into MapMetaData?
    }

    @Override
    protected MapMetaData meta(Map<?, ?> element) {
        return (MapMetaData) super.meta(element);
    }

    public String keyTitle(Map<?, ?> element) {
        MapMetaData meta = meta(element);
        return (meta == null) ? "Key" : meta.keyTitle();
    }

    public String valueTitle(Map<?, ?> element) {
        MapMetaData meta = meta(element);
        return (meta == null) ? "Value" : meta.valueTitle();
    }
}
