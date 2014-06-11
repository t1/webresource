package com.github.t1.webresource.html;

import java.net.URI;

public class A extends Part {
    public A(Tag container) {
        super(container, "a", false);
    }

    public A href(URI uri) {
        attribute("href", uri);
        return this;
    }
}
