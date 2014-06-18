package com.github.t1.webresource.codec2;

import com.github.t1.webresource.html.Part;
import com.github.t1.webresource.meta2.Primitive;

public class PrimitiveHtmlPartWriter implements HtmlPartWriter<Primitive<Object>> {
    @Override
    public void write(Primitive<Object> primitive, Part container) {
        container.write(primitive.getObject().toString());
    }
}
