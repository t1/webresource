package com.github.t1.webresource.model;

import org.junit.*;

@Ignore("TODO switch to jax-rs client api")
public class TagIT {
    // private static final String BASE_URL = PersonWebResourceIT.BASE_URL;
    private static final String XML_HEADER = PersonWebResourceIT.XML_HEADER;

    private static final String XML = XML_HEADER + "\n" //
            + "<tag name=\"X\">hiho</tag>\n";

    @Test
    public void integrationTest() {
        // String initial = client.getTags();
        // long created = create();
        // assertFalse(initial.equals(client.getTags()));
        // assertEquals(XML_HEADER + "<tag id=\"" + created + "\" name=\"X\">hiho</tag>", client.getTag(created));
        // client.deleteTag(created);
        // assertEquals(initial, client.getTags());
    }

    // private static long create() {
    // ClientResponse<String> result = client.createTag(XML);
    // result.getEntity();
    // String location = result.getHeaders().getFirst("Location");
    // assert location.startsWith(BASE_URL);
    // location = location.substring(BASE_URL.length());
    // assert location.startsWith("tag/");
    // location = location.substring(4);
    // return Long.valueOf(location);
    // }
}
