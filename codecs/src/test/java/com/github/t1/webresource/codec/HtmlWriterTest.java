package com.github.t1.webresource.codec;

import static java.util.Arrays.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class HtmlWriterTest extends AbstractHtmlWriterTest {
    @Test
    public void shouldEncodeNullObject() throws Exception {
        writer(null).write();

        assertEquals(wrapped(""), result());
    }

    @Test
    public void shouldEncodePrimitiveString() throws Exception {
        writer("dummy").write();

        assertEquals(wrapped("dummy"), result());
    }

    @Test
    public void shouldEscapeString() throws Exception {
        writer("string & ampersand").write();

        assertEquals(wrapped("string &amp; ampersand"), result());
    }

    @Test
    public void shouldEncodePrimitiveInteger() throws Exception {
        writer(1234).write();

        assertEquals(wrapped("1234"), result());
    }

    @Test
    public void shouldEncodeList() throws Exception {
        List<String> list = asList("one", "two", "three");

        writer(list).write();

        assertEquals(wrapped(ul("strings", "one", "two", "three")), result());
    }
}
