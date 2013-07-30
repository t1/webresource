package com.github.t1.webresource.model;

import static org.junit.Assert.*;

import java.io.*;

import javax.xml.bind.JAXB;

import org.junit.Test;

import com.github.t1.webresource.model.Tag;

public class TagTest {
    private static final String XML_HEADER = PersonWebResourceIT.XML_HEADER;

    private static final String XML = XML_HEADER //
            + "<tag key=\"X\">hiho</tag>\n";
    private static final Tag TAG = new Tag("X", "hiho");

    @Test
    public void shouldMarshal() throws Exception {
        StringWriter writer = new StringWriter();

        JAXB.marshal(TAG, writer);

        assertEquals(XML, writer.toString());
    }

    @Test
    public void shouldUnmarshal() throws Exception {
        Tag tag = JAXB.unmarshal(new StringReader(XML), Tag.class);

        assertEquals(TAG, tag);
    }

    @Test
    public void twoEqualTagsShouldBeEqual() throws Exception {
        Tag tag2 = new Tag("X", "hiho");

        assertEquals(TAG, tag2);
    }

    @Test
    public void twoEqualTagsShouldHaveSameHash() throws Exception {
        Tag tag2 = new Tag("X", "hiho");

        assertEquals(TAG.hashCode(), tag2.hashCode());
    }

    @Test
    public void twoEqualTagsShouldHaveSameToString() throws Exception {
        Tag tag2 = new Tag("X", "hiho");

        assertEquals(TAG.toString(), tag2.toString());
    }
}
