package com.github.t1.webresource.codec2;

import java.net.URI;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import com.github.t1.webresource.accessors.*;
import com.github.t1.webresource.html.*;

@RequiredArgsConstructor
public class HtmlLinkPartWriter implements HtmlPartWriter {
    @Inject
    private BasePath basePath;
    @Inject
    private Accessors accessors;

    private final URI item;

    @Override
    public void writeTo(Part container) {
        Accessor<URI> accessor = accessors.of(item);
        String title = accessor.title(item);
        URI link = accessor.link(item);
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
