package com.github.t1.webresource.codec2;

import com.github.t1.webresource.html.Part;

public interface HtmlPartWriter<T> {
    void write(T object, Part container);
}
