package com.github.t1.webresource.codec;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.MessageBodyWriter;

import lombok.extern.slf4j.Slf4j;

import com.github.t1.webresource.meta.Items;

/** Binding for a {@link HtmlWriter} to JAX-RS */
@Slf4j
@javax.ws.rs.ext.Provider
@javax.ws.rs.Produces("text/html")
public class HtmlMessageBodyWriter implements MessageBodyWriter<Object> {

    @Inject
    Instance<HtmlWriter> htmlWriter;

    @Context
    @javax.enterprise.inject.Produces
    @RequestScoped
    UriInfo uriInfo;

    @javax.enterprise.inject.Produces
    @RequestScoped
    @HtmlWriterQualifier
    private Writer out;

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
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException,
            WebApplicationException {
        log.debug("start html-encoding");

        try {
            out = new OutputStreamWriter(entityStream);
            htmlWriter.get().write(Items.newItem(t));
        } catch (RuntimeException e) {
            log.error("error while encoding", e);
            throw e;
        } finally {
            out.flush(); // doesn't work without this :-/
            out = null;
            log.debug("done html-encoding");
        }
    }
}
