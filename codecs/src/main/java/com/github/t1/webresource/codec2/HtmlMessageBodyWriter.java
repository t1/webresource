package com.github.t1.webresource.codec2;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.inject.Inject;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.MessageBodyWriter;

import com.github.t1.webresource.accessors.*;
import com.github.t1.webresource.html.*;

@javax.ws.rs.ext.Provider
@javax.ws.rs.Produces("text/html")
public class HtmlMessageBodyWriter implements MessageBodyWriter<Object> {
    @Inject
    private Accessors accessors;
    @Inject
    private HtmlPartResover parts;

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public long getSize(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) {
        PrintWriter writer = new PrintWriter(entityStream);
        try (Html html = new Html(writer)) {
            try (Head head = html.head()) {
                printHead(t, head);
            }
            try (Body body = html.body()) {
                printHeadline(t, body);
                parts.of(t).writeTo(body);
            }
        }
        writer.flush(); // JBoss doesn't work without :(
    }

    private void printHead(Object t, Head head) {
        Accessor<Object> accessor = accessors.of(t);
        String titleString = accessor.title(t);
        if (titleString != null) {
            try (Title titleTag = head.title()) {
                titleTag.write(titleString);
            }
        }
    }

    private void printHeadline(Object t, Body body) {
        Accessor<Object> accessor = accessors.of(t);
        String title = accessor.title(t);
        if (title != null) {
            try (H1 h1 = body.h1()) {
                h1.write(title);
            }
        }
    }
}
