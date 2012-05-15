package com.github.t1.webresource;

import static org.junit.Assert.*;

import java.util.*;
import java.util.regex.*;

import javax.ws.rs.*;

import org.jboss.resteasy.client.*;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Test;

public class PersonWebResourceTest {
    static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
    static final String BASE_URL = "http://localhost:8080/webresource-demo/";
    private static final Pattern PERSON = Pattern.compile("<person id=\"(?<id>[0-9]+)\"><first>(?<first>\\w*)</first><last>(?<last>\\w*)</last></person>(?<overflow>.*)");

    public interface PersonService {
        @GET
        @Path("persons")
        @Produces("text/xml")
        String getPersons();

        @GET
        @Path("person/{id}")
        @Produces("text/xml")
        String getPerson(@PathParam("id") long id);

        @POST
        @Path("persons")
        @Consumes("text/xml")
        public ClientResponse<String> createPerson(String person);

        @PUT
        @Path("person/{id}")
        @Consumes("text/xml")
        @Produces("text/xml")
        public String updatePerson(@PathParam("id") long id, String person);

        @DELETE
        @Path("person/{id}")
        @Produces("text/xml")
        public String deletePerson(@PathParam("id") long id);
    }

    static {
        RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
    }

    public static void main(String[] args) {
        new PersonWebResourceTest().run();
    }

    private final PersonService client = ProxyFactory.create(PersonService.class, BASE_URL);

    @Test
    public void run() {
        List<Long> before = getAll();

        long id = create();
        List<Long> created = getAll();
        assertEquals(before.size() + 1, created.size());
        assertTrue(created.contains(id));

        match(client.getPerson(id), id, "Joe");

        String updated = client.updatePerson(id, "" //
                + "<person>" //
                + "  <first>Jim</first>" //
                + "  <last>Doe</last>" //
                + "</person>");
        match(updated, id, "Jim");

        match(client.getPerson(id), id, "Jim");

        match(client.deletePerson(id), id, "Jim");

        assertEquals(before, getAll());
    }

    private void match(String xml, long id, String first) {
        Matcher m = PERSON.matcher(stripHeader(xml));
        assertTrue(m.matches());
        assertEquals(id, Long.parseLong(m.group("id")));
        assertEquals(first, m.group("first"));
        assertEquals("Doe", m.group("last"));
    }

    private List<Long> getAll() {
        String xml = client.getPersons();
        xml = stripHeader(xml);
        if ("<collection/>".equals(xml))
            return Collections.emptyList();
        assertTrue(xml.startsWith("<collection>"));
        xml = xml.substring(12);
        assertTrue(xml.endsWith("</collection>"));
        xml = xml.substring(0, xml.length() - 13);

        List<Long> result = new ArrayList<>();
        while (!xml.isEmpty()) {
            Matcher m = PERSON.matcher(xml);
            assertTrue(m.matches());
            Long id = Long.parseLong(m.group("id"));
            result.add(id);
            xml = m.group("overflow");
        }

        return result;
    }

    private String stripHeader(String xml) {
        if (xml.startsWith(XML_HEADER))
            xml = xml.substring(XML_HEADER.length());
        return xml;
    }

    private long create() {
        ClientResponse<String> result = client.createPerson("" //
                + "<person>" //
                + "  <first>Joe</first>" //
                + "  <last>Doe</last>" //
                // + "  <tags>" //
                // + "    <tag name=\"pet\">cat</tag>" //
                // + "  </tags>" //
                + "</person>");
        result.getEntity();
        String location = result.getHeaders().getFirst("Location");
        assert location.startsWith(BASE_URL);
        location = location.substring(BASE_URL.length());
        assert location.startsWith("person/");
        location = location.substring(7);
        return Long.valueOf(location);
    }
}
