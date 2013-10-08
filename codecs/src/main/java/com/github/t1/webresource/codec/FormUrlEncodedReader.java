package com.github.t1.webresource.codec;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;

import lombok.extern.slf4j.Slf4j;

/** Binding for a {@link FormUrlDecoder} to JAX-RS */
@Slf4j
@Provider
@Consumes("application/x-www-form-urlencoded")
public class FormUrlEncodedReader implements MessageBodyReader<Object> {
    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException,
            WebApplicationException {
        log.debug("start url-decoding {}, {}: {}", type, mediaType, annotations);
        BufferedReader in = new BufferedReader(new InputStreamReader(entityStream));
        try {
            return new FormUrlDecoder<Object>(type).read(in);
        } catch (RuntimeException | IOException e) {
            log.error("error while decoding", e);
            throw e;
        } finally {
            log.debug("done url-decoding");
        }
    }
}
