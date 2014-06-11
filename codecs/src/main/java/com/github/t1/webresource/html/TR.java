package com.github.t1.webresource.html;

public class TR extends Tag {
    TR(Tag container) {
        super(container, "tr", true);
    }

    public TD td() {
        return new TD(this);
    }
}
