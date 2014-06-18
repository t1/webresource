package com.github.t1.webresource.meta2;

import java.net.URI;
import java.util.List;

public class ListAccessor extends AbstractAccessor<List<?>> {
    @Override
    public URI link(List<?> element) {
        return null; // TODO put into ListMetaData
    }

    @Override
    protected ListMetaData meta(List<?> element) {
        return (ListMetaData) super.meta(element);
    }
}
