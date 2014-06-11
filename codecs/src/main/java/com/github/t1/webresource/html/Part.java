package com.github.t1.webresource.html;


public class Part extends Tag {
    Part(Tag container, String tagName, boolean nl) {
        super(container, tagName, nl);
    }

    public H1 h1() {
        return new H1(this);
    }

    public UL ul() {
        return new UL(this);
    }

    public A a() {
        return new A(this);
    }

    public Table table() {
        return new Table(this);
    }
}
