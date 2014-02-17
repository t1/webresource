package com.github.t1.webresource.accessors;

import java.net.URI;

public interface Accessor<T> {
    public String title(T element);

    public URI link(T element);
}
