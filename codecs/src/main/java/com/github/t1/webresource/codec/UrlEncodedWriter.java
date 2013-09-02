package com.github.t1.webresource.codec;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;

import lombok.extern.slf4j.Slf4j;

/** Binding for a {@link UrlEncoder} to JAX-RS */
@Slf4j
@Provider
@Produces("application/x-www-form-urlencoded")
public class UrlEncodedWriter implements MessageBodyWriter<Object> {

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
        log.debug("start url-encoding");
        Writer out = new OutputStreamWriter(entityStream);
        try {
            new UrlEncoder(out).write(t);
        } catch (RuntimeException | IOException e) {
            log.error("error while encoding", e);
            throw e;
        } finally {
            out.flush(); // doesn't work without this :-(
            log.debug("done url-encoding");
        }
    }
}
