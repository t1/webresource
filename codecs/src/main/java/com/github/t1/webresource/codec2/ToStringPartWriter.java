package com.github.t1.webresource.codec2;

import com.github.t1.webresource.html.Part;

public class ToStringPartWriter implements HtmlPartWriter {

    private final Object item;

    public ToStringPartWriter(Object item) {
        this.item = item;
    }

    @Override
    public void writeTo(Part container) {
        container.write(item.toString());
    }
}
