package com.github.t1.webresource.codec2;

import com.github.t1.webresource.html.Part;

public class ToStringPartWriter implements HtmlPartWriter<Object> {
    @Override
    public void write(Object object, Part container) {
        container.write(object.toString());
    }
}
