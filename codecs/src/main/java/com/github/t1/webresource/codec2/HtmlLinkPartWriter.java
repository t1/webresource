package com.github.t1.webresource.codec2;

import java.net.URI;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import com.github.t1.webresource.html.*;
import com.github.t1.webresource.meta2.*;

@RequiredArgsConstructor
public class HtmlLinkPartWriter implements HtmlPartWriter<URI> {
    @Inject
    private BasePath basePath;
    @Inject
    private Accessors accessors;

    @Override
    public void write(URI item, Part container) {
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
