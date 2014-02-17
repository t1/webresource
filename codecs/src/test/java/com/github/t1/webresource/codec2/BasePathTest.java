package com.github.t1.webresource.codec2;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.net.URI;

import javax.ws.rs.core.UriInfo;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class BasePathTest {
    private static final String BASE = "http://localhost";

    @InjectMocks
    private BasePath basePath;
    @Mock
    private UriInfo uriInfo;
    @Mock
    private UriEscaper uriEscaper;

    @Before
    public void before() {
        when(uriInfo.getBaseUri()).thenReturn(URI.create(BASE));
        when(uriEscaper.escape(anyString())).then(new Answer<URI>() {
            @Override
            public URI answer(InvocationOnMock invocation) throws Throwable {
                String arg0 = (String) invocation.getArguments()[0];
                return new URI(null, arg0, null);
            }
        });
    }

    @Test
    public void shouldResolveSimplePath() throws Exception {
        URI resolved = basePath.resolve("path");

        assertEquals(BASE + "/path", resolved.toASCIIString());
    }
}
