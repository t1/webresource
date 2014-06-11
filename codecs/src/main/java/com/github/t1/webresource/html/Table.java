package com.github.t1.webresource.html;

public class Table extends Tag {
    Table(Tag container) {
        super(container, "table", true);
    }

    public TR tr() {
        return new TR(this);
    }
}
