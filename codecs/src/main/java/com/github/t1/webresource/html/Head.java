package com.github.t1.webresource.html;


public class Head extends Tag {
    public Head(Tag container) {
        super(container, "head", false);
    }

    public Title title() {
        return new Title(this);
    }
}
