package com.github.t1.webresource.model;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.*;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.ws.rs.client.*;
import javax.ws.rs.core.*;
import java.util.*;
import java.util.regex.*;

import static javax.ws.rs.core.MediaType.*;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
@Ignore("TODO finish update to jax-rs client api")
public class PersonWebResourceIT {
    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, PersonWebResourceIT.class.getName() + ".war") //
                .addClasses(Person.class, Tag.class).addClass(Person.class.getName() + "WebResource") //
                ;
    }

    static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
    private static final Pattern PERSON =
            Pattern.compile(
                    "<person id=\"(?<id>[0-9]+)\"><first>(?<first>\\w*)</first><last>(?<last>\\w*)</last></person>(?<overflow>.*)");

    public static void main(String[] args) {
        new PersonWebResourceIT().run();
    }

    private static final Client CLIENT = ClientBuilder.newClient();

    private WebTarget app;
    private final WebTarget persons = app.path("persons/{id}");

    @Inject
    UriInfo uriInfo;

    @Before
    public void before() {
        app = CLIENT.target(uriInfo.getAbsolutePath());
    }

    @Test
    public void shouldGetPerson() {
        Invocation.Builder request = persons.resolveTemplate("id", "1").request(APPLICATION_XML);

        Person person = request.get(Person.class);

        assertEquals("Albus", person.getFirst());
        assertEquals("Dumbledore", person.getLast());
        assertEquals(1, person.getTags().size());
        // assertEquals("Teacher", person.getTags().get(0).getKey());
    }

    @Test
    public void run() {
        System.out.println("------------------------------------ getAll");
        List<Long> before = getAll();

        System.out.println("------------------------------------ create");
        long id = create();

        System.out.println("------------------------------------ getAll");
        List<Long> created = getAll();
        assertEquals(before.size() + 1, created.size());
        assertTrue(created.contains(id));

        // match(CLIENT.getPerson(id), id, "Joe");

        System.out.println("------------------------------------ update");
        // String updated = CLIENT.updatePerson(id, "" //
        // + "<person>" //
        // + "  <first>Jim</first>" //
        // + "  <last>Doe</last>" //
        // + "</person>");
        // match(updated, id, "Jim");

        System.out.println("------------------------------------ get");
        // match(CLIENT.getPerson(id), id, "Jim");

        System.out.println("------------------------------------ extension");
        // assertEquals("hello extension", CLIENT.getExtension());

        System.out.println("------------------------------------ delete");
        // match(CLIENT.deletePerson(id), id, "Jim");

        assertEquals(before, getAll());
        System.out.println("------------------------------------ done");
    }

    private void match(String xml, long id, String first) {
        Matcher m = PERSON.matcher(stripHeader(xml));
        assertTrue(m.matches());
        assertEquals(id, Long.parseLong(m.group("id")));
        assertEquals(first, m.group("first"));
        assertEquals("Doe", m.group("last"));
    }

    private List<Long> getAll() {
        System.out.println("-------- getPersons");
        String xml = "";// CLIENT.getPersons();
        System.out.println(xml);
        System.out.println("-------- stripHeader");
        xml = stripHeader(xml);
        System.out.println(xml);
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
        Response result = persons.request().put(Entity.entity("" //
                + "<person>" //
                + "  <first>Joe</first>" //
                + "  <last>Doe</last>" //
                // + "  <tags>" //
                // + "    <tag name=\"pet\">cat</tag>" //
                // + "  </tags>" //
                + "</person>", APPLICATION_XML));
        result.getEntity();
        // String location = result.getResponseHeaders().getFirst("Location");
        // assert location.startsWith(BASE_URL);
        // location = location.substring(BASE_URL.length());
        // assert location.startsWith("person/");
        // location = location.substring(7);
        return 3L; // Long.valueOf(location);
    }
}
