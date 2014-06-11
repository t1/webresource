package com.github.t1.webresource.codec2;

import static java.util.Arrays.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.*;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.t1.webresource.accessors.*;
import com.github.t1.webresource.html.Html;

@RunWith(Arquillian.class)
public class HtmlMessageBodyWriterTest {
    @Deployment
    public static JavaArchive loggingInterceptorDeployment() {
        return ShrinkWrap.create(JavaArchive.class) //
                .addPackage(HtmlMessageBodyWriter.class.getPackage()) //
                .addPackage(Accessors.class.getPackage()) //
                .addPackage(Html.class.getPackage()) //
                .addPackage(UriInfo.class.getPackage()) //
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml") //
        ;
    }

    @Inject
    HtmlMessageBodyWriter writer;
    @Inject
    Accessors accessors;
    @Inject
    HtmlPartResover parts;
    @Inject
    MetaDataStore meta;

    @Produces
    UriInfo uriInfo = mock(UriInfo.class);

    private final ByteArrayOutputStream stream = new ByteArrayOutputStream();

    private String html(String title, String body) {
        return "<html>\n" //
                + "<head><title>" + title + "</title>\n" //
                + "</head>\n" //
                + "<body>\n" //
                + "<h1>" + title + "</h1>\n" //
                + body //
                + "</body>\n" //
                + "</html>\n";
    }

    @Test
    public void shouldProduceHtmlFromLink() {
        URI uri = URI.create("http://example.com/");
        when(uriInfo.getBaseUri()).thenReturn(uri);
        meta.put(uri, new UriMetaData("some link"));

        writer.writeTo(uri, URI.class, null, null, null, null, stream);

        assertEquals(html("some link", "<a href=\"http://example.com/\">some link</a>\n"), stream.toString());
    }

    @Test
    public void shouldProduceHtmlFromList() {
        List<String> list = asList("one", "two", "three");
        meta.put(list, new ListMetaData("some list"));

        writer.writeTo(list, List.class, null, null, null, null, stream);

        assertEquals(html("some list", "<ul>\n" //
                + "<li>one</li>\n" //
                + "<li>two</li>\n" //
                + "<li>three</li>\n" //
                + "</ul>\n" //
        ), stream.toString());
    }

    @Test
    public void shouldProduceHtmlFromMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("one", "111");
        map.put("two", "222");
        map.put("three", "333");
        meta.put(map, new MapMetaData("some map", "some key", "some value"));

        writer.writeTo(map, Map.class, null, null, null, null, stream);

        assertEquals(html("some map", "<table>\n" //
                + "<tr>\n" //
                + "<td>some key</td>\n" //
                + "<td>some value</td>\n" //
                + "</tr>\n" //
                + "<tr>\n" //
                + "<td>one</td>\n" //
                + "<td>111</td>\n" //
                + "</tr>\n" //
                + "<tr>\n" //
                + "<td>two</td>\n" //
                + "<td>222</td>\n" //
                + "</tr>\n" //
                + "<tr>\n" //
                + "<td>three</td>\n" //
                + "<td>333</td>\n" //
                + "</tr>\n" //
                + "</table>\n" //
        ), stream.toString());
    }
}
