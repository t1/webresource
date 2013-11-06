package com.github.t1.webresource.codec;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.net.URI;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.t1.webresource.codec.HtmlOut.Attribute;
import com.github.t1.webresource.meta.*;

@RunWith(MockitoJUnitRunner.class)
public class HrefAttributeTest {

    @Mock
    UriResolver resolver;
    @InjectMocks
    HrefAttribute href = new HrefAttribute();

    @Test
    public void shouldResolve() throws Exception {
        Item simpleItem = Items.newItem("simple");
        when(resolver.resolveBase(anyString())).thenReturn(URI.create("resolved-dummy"));

        Attribute attribute = href.to(simpleItem);

        assertEquals("href", attribute.getName());
        assertEquals("resolved-dummy", attribute.getValue());
    }

    @Test
    public void shouldResolveSimple() throws Exception {
        Item simpleItem = Items.newItem("simple");

        href.to(simpleItem);

        verify(resolver).resolveBase("strings/simple");
    }

    @Test
    public void shouldResolveType() throws Exception {
        Item simpleItem = Items.newItem(String.class);

        href.to(simpleItem);

        verify(resolver).resolveBase("strings");
    }
}
