package com.github.t1.webresource.codec2;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.MessageBodyWriter;

import com.github.t1.webresource.accessors.*;
import com.github.t1.webresource.html.*;

public abstract class AbstractHtmlMessageBodyWriter<T> implements MessageBodyWriter<T> {
    @Inject
    private BasePath basePath;
    @Inject
    protected Accessors accessors;

    @Override
    public long getSize(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) {
        PrintWriter writer = new PrintWriter(entityStream);
        try (Html html = new Html(writer)) {
            try (Head head = html.head()) {
                printHead(t, head);
            }
            try (Body body = html.body()) {
                printHeadline(t, body);
                printBody(t, writer);
            }
        }
        writer.flush(); // JBoss doesn't work without :(
    }

    private void printHead(T t, Head head) {
        Accessor<T> accessor = accessors.of(t);
        String titleString = accessor.title(t);
        if (titleString != null) {
            try (Title titleTag = head.title()) {
                head.write(titleString);
            }
        }
    }

    protected void printHeadline(T t, Body body) {
        Accessor<T> accessor = accessors.of(t);
        String title = accessor.title(t);
        if (title != null) {
            try (H1 h1 = body.h1()) {
                h1.write(title);
            }
        }
    }

    protected abstract void printBody(T t, PrintWriter out);

    protected void printItem(Object item, PrintWriter out) {
        Accessor<Object> accessor = accessors.of(item);
        String title = accessor.title(item);
        URI link = accessor.link(item);
        out.append((link == null) ? title : link(link, title));
    }

    protected String link(String path, String label) {
        return link(basePath.resolve(path), label);
    }

    protected String link(URI uri, String label) {
        if (label == null)
            return "<a href=\"" + uri + "\"/>";
        return "<a href=\"" + uri + "\">" + label + "</a>";
    }
}
