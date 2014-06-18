package com.github.t1.webresource.codec2;

import java.net.URI;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import com.github.t1.webresource.html.*;
import com.github.t1.webresource.meta2.*;

@RequiredArgsConstructor
public class UriHtmlPartWriter implements HtmlPartWriter<Primitive<URI>> {
    @Inject
    private BasePath basePath;
    @Inject
    private Accessors accessors;

    @Override
    public void write(Primitive<URI> primitive, Part container) {
        URI uri = primitive.getObject();
        Accessor<URI> accessor = accessors.of(uri);
        String title = accessor.title(uri);
        URI link = accessor.link(uri);
        basePath.resolve(link); // TODO why this?
        link(link, title, container);
    }

    private void link(URI uri, String label, Part container) {
        try (A a = container.a().href(uri)) {
            if (label != null) {
                a.write(label);
            }
        }
    }
}
