package com.github.t1.webresource.accessors;

import java.net.URI;

import javax.inject.Inject;

import com.github.t1.webresource.codec2.BasePath;

public abstract class AbstractAccessor<T> implements Accessor<T> {
    @Inject
    private MetaDataStore metaDataStore;
    @Inject
    private BasePath basePath;

    @Override
    public String title(T element) {
        MetaData<T> meta = meta(element);
        return (meta == null) ? null : meta.title();
    }

    protected MetaData<T> meta(T element) {
        return metaDataStore.get(element);
    }

    protected URI resolve(String path) {
        return basePath.resolve(path);
    }
}
