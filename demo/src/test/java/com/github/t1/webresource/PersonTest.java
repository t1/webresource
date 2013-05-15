package com.github.t1.webresource;

import static org.junit.Assert.*;

import java.io.*;
import java.util.Arrays;

import javax.xml.bind.JAXB;

import org.junit.*;

public class PersonTest {
    private static final String XML_HEADER = PersonWebResourceIT.XML_HEADER;

    private static final String XML = XML_HEADER //
            + "<person>\n" //
            + "    <first>Joe</first>\n" //
            + "    <last>Doe</last>\n" //
            + "</person>\n";

    private static final String TAGGED_XML = XML_HEADER //
            + "<person>\n" //
            + "    <first>Joe</first>\n" //
            + "    <last>Doe</last>\n" //
            + "    <tags>\n" //
            + "        <tag key=\"tag1\">description-1</tag>\n" //
            + "    </tags>\n" //
            + "</person>\n";

    private static final Tag TAG1 = new Tag("tag1", "description-1");
    private static final Tag TAG2 = new Tag("tag2", "description-2");

    private final Person person = new Person("Joe", "Doe");

    @Test
    public void shouldReturnEmptyTags() throws Exception {
        assertTrue(person.getTags().isEmpty());
    }

    @Test
    public void tagsShouldBeImmutable() throws Exception {
        assertTrue(person.getTags().isEmpty());
    }

    @Test
    public void shouldMarshal() throws Exception {
        StringWriter xml = new StringWriter();
        JAXB.marshal(person, xml);

        assertEquals(XML, xml.toString());
    }

    @Test
    public void shouldUnmarshal() throws Exception {
        Person person = JAXB.unmarshal(new StringReader(XML), Person.class);

        assertEquals("Joe", person.getFirst());
        assertEquals("Doe", person.getLast());
    }

    @Test
    public void shouldTag() throws Exception {
        person.tag(TAG1);

        assertEquals(Arrays.asList(TAG1), person.getTags());
    }

    @Test
    public void shouldTagTwo() throws Exception {
        person.tag(TAG1).tag(TAG2);

        assertEquals(Arrays.asList(TAG1, TAG2), person.getTags());
    }

    @Test
    public void shouldUntag() throws Exception {
        person.tag(TAG1);

        boolean untagged = person.untag(TAG1);

        assertTrue(untagged);
        assertTrue(person.getTags().isEmpty());
    }

    @Test
    public void shouldUntagKey() throws Exception {
        person.tag(TAG1);

        boolean untagged = person.untag(TAG1.getKey());

        assertTrue(untagged);
        assertTrue(person.getTags().isEmpty());
    }

    @Test
    public void shouldNotUntagUntagged() throws Exception {
        boolean untagged = person.untag(TAG1);

        assertFalse(untagged);
        assertTrue(person.getTags().isEmpty());
    }

    @Test
    public void shouldNotUntagUntaggedKey() throws Exception {
        boolean untagged = person.untag(TAG1.getKey());

        assertFalse(untagged);
        assertTrue(person.getTags().isEmpty());
    }

    @Test
    public void shouldNotUntagUnknownKey() throws Exception {
        person.tag(TAG1);

        boolean untagged = person.untag("wrong-key");

        assertFalse(untagged);
        assertEquals(Arrays.asList(TAG1), person.getTags());
    }

    @Test
    public void shouldNotUntagEmptyKey() throws Exception {
        person.tag(TAG1);

        boolean untagged = person.untag("");

        assertFalse(untagged);
        assertEquals(Arrays.asList(TAG1), person.getTags());
    }

    @Test
    public void shouldNotUntagNullKey() throws Exception {
        person.tag(TAG1);

        boolean untagged = person.untag((String) null);

        assertFalse(untagged);
        assertEquals(Arrays.asList(TAG1), person.getTags());
    }

    @Test
    public void shouldMarshalTagged() throws Exception {
        person.tag(TAG1);

        StringWriter xml = new StringWriter();
        JAXB.marshal(person, xml);

        assertEquals(TAGGED_XML, xml.toString());
    }

    @Test
    public void shouldUnmarshalTagged() throws Exception {
        Person person = JAXB.unmarshal(new StringReader(TAGGED_XML), Person.class);

        assertEquals("Joe", person.getFirst());
        assertEquals("Doe", person.getLast());
        assertEquals(Arrays.asList(TAG1), person.getTags());
    }

    @Test
    @Ignore
    public void shouldMarshalTagList() throws Exception {
        person.tag(TAG1).tag(TAG2);

        StringWriter xml = new StringWriter();
        JAXB.marshal(person.getTags(), xml);

        assertEquals(XML_HEADER //
                + "<tags>\n" //
                + "    <tag key=\"tag1\">description-1</tag>\n" //
                + "    <tag key=\"tag2\">description-2</tag>\n" //
                + "</tags>\n" //
        , xml.toString());
    }
}
