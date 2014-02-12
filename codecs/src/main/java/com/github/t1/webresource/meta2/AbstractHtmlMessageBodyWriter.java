package com.github.t1.webresource.meta2;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.MessageBodyWriter;

public abstract class AbstractHtmlMessageBodyWriter<T> implements MessageBodyWriter<T> {
    @Inject
    private BasePath basePath;
    @Inject
    private Accessors accessors;

    @Override
    public long getSize(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) {
        PrintWriter out = new PrintWriter(entityStream);
        out.println("<html><head>");
        printHead(t, out);
        out.println("</head><body>");
        printBody(t, out);
        out.println("</body></html>");
        out.flush(); // JBoss doesn't work without :(
    }

    private void printHead(T t, PrintWriter out) {
        String title = title(t);
        if (title != null) {
            out.append("<title>").append(title).println("</title>");
        }
    }

    protected String title(T t) {
        return null;
    }

    protected abstract void printBody(T t, PrintWriter out);


    protected void printItem(Object item, PrintWriter out) {
        Accessor<Object> accessor = accessors.of(item);
        String title = accessor.title(item);
        URI link = accessor.link(item);
        if (link == null) {
            out.append(title);
        } else {
            out.printf(link(link, title));
        }
    }

    protected String link(String path, String label) {
        return link(basePath.resolve(path), label);
    }

    protected String link(URI uri, String label) {
        return "<a href=\"" + uri + "\">" + label + "</a>";
    }
}
