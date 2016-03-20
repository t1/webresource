package com.github.t1.webresource.model;

import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.*;

import static javax.ws.rs.core.MediaType.*;
import static javax.ws.rs.core.Response.Status.*;

/**
 * Serve static resources from <code>src/main/resources</code>. Necessary, if your jax-rs endpoint 'hides' the files
 * in `webapp`.
 */
@Slf4j
@Path("/")
public class StaticResources {
    @GET
    @Path("{name:.*\\.css}")
    public Response getStaticResource(@PathParam("name") String name) throws IOException {
        log.debug("get static resource {}", name);
        InputStream stream = classLoader().getResourceAsStream("/" + name);
        if (stream == null)
            return Response.status(NOT_FOUND)
                           .entity("resource '" + name + "' not found\n")
                           .type(TEXT_PLAIN)
                           .build();
        log.debug("found {}", name);
        return Response.ok(stream).build();
    }

    private ClassLoader classLoader() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null)
            loader = getClass().getClassLoader();
        return loader;
    }
}
