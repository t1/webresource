package com.github.t1.webresource.meta2;

import java.net.URI;
import java.util.List;

public class ListAccessor<T> extends AbstractAccessor<List<T>> {
    @Override
    public URI link(List<T> element) {
        return null; // TODO put into ListMetaData
    }
}
