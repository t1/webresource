package com.github.t1.webresource.codec;

import static java.util.Arrays.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.github.t1.webresource.meta.Items;

public class HtmlBodyWriterTest extends AbstractHtmlWriterTest {
    private void write(Object t) {
        HtmlBodyWriter writer = new HtmlBodyWriter();
        writer.out = out;
        writer.write(Items.newItem(t));
    }

    @Test
    public void shouldEncodeNullObject() throws Exception {
        write(null);

        assertEquals("", result());
    }

    @Test
    public void shouldEncodePrimitiveString() throws Exception {
        write("dummy");

        assertEquals("dummy", result());
    }

    @Test
    public void shouldEscapeString() throws Exception {
        write("string & ampersand");

        assertEquals("string &amp; ampersand", result());
    }

    @Test
    public void shouldEncodePrimitiveInteger() throws Exception {
        write(1234);

        assertEquals("1234", result());
    }

    @Test
    public void shouldEncodeList() throws Exception {
        List<String> list = asList("one", "two", "three");

        write(list);

        assertEquals(ul("strings", "one", "two", "three"), result());
    }
}
