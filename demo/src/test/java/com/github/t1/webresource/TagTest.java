package com.github.t1.webresource;

import static org.junit.Assert.*;

import java.io.StringWriter;

import javax.xml.bind.JAXB;

import org.junit.Test;

public class TagTest {
    private static final String XML_HEADER = PersonWebResourceIT.XML_HEADER;

    private static final String XML = XML_HEADER + "\n" //
            + "<tag name=\"X\">hiho</tag>\n";
    private static final Tag TAG = new Tag("X", "hiho");

    @Test
    public void toXml() throws Exception {
        StringWriter writer = new StringWriter();

        JAXB.marshal(TAG, writer);

        assertEquals(XML, writer.toString());
    }

    @Test
    public void inPerson() throws Exception {
        Person person = new Person("John", "Smith");
        person.addTag(TAG);

        assertEquals("Person [first=John, last=Smith, tags=[Tag [name=X, description=hiho]]]", person.toString());
    }
}
