package com.github.t1.webresource;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;

import lombok.extern.slf4j.Slf4j;

/** Binding for a {@link HtmlEncoder} to JAX-RS */
@Slf4j
@Provider
@Produces("text/html")
public class HtmlWriter implements MessageBodyWriter<Object> {

    @Context
    UriInfo uriInfo;

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
        Writer out = new OutputStreamWriter(entityStream);
        try {
            new HtmlEncoder(t, out, (uriInfo == null) ? null : uriInfo.getBaseUri()).write();
        } catch (RuntimeException | IOException e) {
            log.error("error while encoding", e);
            throw e;
        } finally {
            out.flush(); // doesn't work without this :-(
            log.debug("done html-encoding");
        }
    }
}
