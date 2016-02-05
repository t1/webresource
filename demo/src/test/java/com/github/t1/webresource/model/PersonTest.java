package com.github.t1.webresource.model;

import static java.util.Arrays.*;
import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

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
    public void shouldReturnEmptyTags() {
        assertTrue(person.getTags().isEmpty());
    }

    @Test
    public void tagsShouldBeImmutable() {
        assertTrue(person.getTags().isEmpty());
    }

    @Test
    public void shouldMarshal() {
        StringWriter xml = new StringWriter();
        JAXB.marshal(person, xml);

        assertEquals(XML, xml.toString());
    }

    @Test
    public void shouldUnmarshal() {
        Person person = JAXB.unmarshal(new StringReader(XML), Person.class);

        assertEquals("Joe", person.getFirst());
        assertEquals("Doe", person.getLast());
    }

    @Test
    public void shouldTag() {
        person.tag(TAG1);

        assertEquals(set(TAG1), person.getTags());
    }

    @SafeVarargs
    private final <T> Set<T> set(T... values) {
        return new LinkedHashSet<>(asList(values));
    }

    @Test
    public void shouldTagTwo() {
        person.tag(TAG1).tag(TAG2);

        assertEquals(set(TAG1, TAG2), person.getTags());
    }

    @Test
    public void shouldUntag() {
        person.tag(TAG1);

        boolean untagged = person.untag(TAG1);

        assertTrue(untagged);
        assertTrue(person.getTags().isEmpty());
    }

    @Test
    public void shouldUntagKey() {
        person.tag(TAG1);

        boolean untagged = person.untag(TAG1.getKey());

        assertTrue(untagged);
        assertTrue(person.getTags().isEmpty());
    }

    @Test
    public void shouldNotUntagUntagged() {
        boolean untagged = person.untag(TAG1);

        assertFalse(untagged);
        assertTrue(person.getTags().isEmpty());
    }

    @Test
    public void shouldNotUntagUntaggedKey() {
        boolean untagged = person.untag(TAG1.getKey());

        assertFalse(untagged);
        assertTrue(person.getTags().isEmpty());
    }

    @Test
    public void shouldNotUntagUnknownKey() {
        person.tag(TAG1);

        boolean untagged = person.untag("wrong-key");

        assertFalse(untagged);
        assertEquals(set(TAG1), person.getTags());
    }

    @Test
    public void shouldNotUntagEmptyKey() {
        person.tag(TAG1);

        boolean untagged = person.untag("");

        assertFalse(untagged);
        assertEquals(set(TAG1), person.getTags());
    }

    @Test
    public void shouldNotUntagNullKey() {
        person.tag(TAG1);

        boolean untagged = person.untag((String) null);

        assertFalse(untagged);
        assertEquals(set(TAG1), person.getTags());
    }

    @Test
    public void shouldMarshalTagged() {
        person.tag(TAG1);

        StringWriter xml = new StringWriter();
        JAXB.marshal(person, xml);

        assertEquals(TAGGED_XML, xml.toString());
    }

    @Test
    public void shouldUnmarshalTagged() {
        Person person = JAXB.unmarshal(new StringReader(TAGGED_XML), Person.class);

        assertEquals("Joe", person.getFirst());
        assertEquals("Doe", person.getLast());
        assertEquals(set(TAG1), person.getTags());
    }

    @Test
    @Ignore
    public void shouldMarshalTagList() {
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
