package com.github.t1.webresource.codec2;

import static org.mockito.Mockito.*;

import javax.enterprise.inject.Produces;
import javax.ws.rs.core.UriInfo;

/** This has to be a CDI bean separate from the test, or we'll get circular bean dependencies */
public class UriInfoMockProducer {
    @Produces
    public static final UriInfo uriInfo = mock(UriInfo.class);
}
