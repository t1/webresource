package com.github.t1.webresource;

import javax.ws.rs.*;

import org.jboss.resteasy.client.*;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

// TODO turn this into an automated integration test
public class PersonWebClient {
    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
    private static final String BASE_URL = "http://localhost:8080/webresource-demo/";

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
        new PersonWebClient().run();
    }

    private final PersonService client = ProxyFactory.create(PersonService.class, BASE_URL);

    public void run() {
        println("GET persons", client.getPersons());
        long id = create();
        println("GET persons", client.getPersons());
        println("PUT " + id, client.updatePerson(id, "" //
                + "<person>" //
                + "  <first>Jim</first>" //
                + "  <last>Doe</last>" //
                + "</person>"));
        println("GET person", client.getPerson(id));
        println("DELETE " + id, client.deletePerson(id));
        println("GET persons", client.getPersons());
    }

    private void println(String prefix, String xml) {
        if (xml.startsWith(XML_HEADER))
            xml = xml.substring(XML_HEADER.length());
        System.out.println(prefix + ": " + xml);
    }

    private long create() {
        ClientResponse<String> result = client.createPerson("" //
                + "<person>" //
                + "  <first>Joe</first>" //
                + "  <last>Doe</last>" //
                + "</person>");
        result.getEntity();
        String location = result.getHeaders().getFirst("Location");
        assert location.startsWith(BASE_URL);
        location = location.substring(BASE_URL.length());
        assert location.startsWith("person/");
        location = location.substring(7);
        println("POST: created", location);
        return Long.valueOf(location);
    }
}
