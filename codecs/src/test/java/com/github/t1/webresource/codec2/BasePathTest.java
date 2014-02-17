package com.github.t1.webresource.codec2;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.net.URI;

import javax.ws.rs.core.UriInfo;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.t1.webresource.codec2.BasePath;

@RunWith(MockitoJUnitRunner.class)
public class BasePathTest {
    private static final String BASE = "http://localhost";

    @InjectMocks
    private BasePath basePath;
    @Mock
    private UriInfo uriInfo;

    @Before
    public void before() {
        when(uriInfo.getBaseUri()).thenReturn(URI.create(BASE));
    }

    @Test
    public void shouldResolveSimplePath() throws Exception {
        URI resolved = basePath.resolve("path");

        assertEquals(BASE + "/path", resolved.toASCIIString());
    }

    @Test
    public void shouldResolvePathWithSlashes() throws Exception {
        URI resolved = basePath.resolve("path/with/slashes");

        assertEquals(BASE + "/path/with/slashes", resolved.toASCIIString());
    }

    @Test
    public void shouldUrlEscapeSpaces() throws Exception {
        URI resolved = basePath.resolve("this contains spaces");

        assertEquals(BASE + "/this%20contains%20spaces", resolved.toASCIIString());
    }

    @Test
    public void shouldUrlEscapeHash() throws Exception {
        URI resolved = basePath.resolve("#");

        assertEquals(BASE + "/%23", resolved.toASCIIString());
    }

    @Test
    public void shouldUrlEscapeCurlies() throws Exception {
        URI resolved = basePath.resolve("{}");

        assertEquals(BASE + "/%7B%7D", resolved.toASCIIString());
    }

    @Test
    public void shouldUrlNotEscapeReserved() throws Exception {
        URI resolved = basePath.resolve(";/?:@&=+$,");

        assertEquals(BASE + "/;/?:@&=+$,", resolved.toASCIIString());
    }

    @Test
    @Ignore
    public void shouldUrlEscapeBrackets() throws Exception {
        URI resolved = basePath.resolve("[]");

        assertEquals(BASE + "/%5B%5D", resolved.toASCIIString());
    }
}
