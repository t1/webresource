package com.github.t1.webresource.codec;

import static org.mockito.Mockito.*;

import java.io.*;
import java.net.URI;
import java.util.List;

import javax.persistence.Id;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.*;

import lombok.*;

import com.github.t1.webresource.meta.*;

abstract class AbstractHtmlWriterTest {
    public static final String BASE_URI = "http://localhost:8080/demo/resource/";

    private final Writer out = new StringWriter();

    public void write(Class<? extends AbstractHtmlWriter> type, Object t) {
        try {
            write(type.newInstance(), t);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(AbstractHtmlWriter writer, Object t) {
        writer.out = out;
        writer.uriResolver = mockUriResolver();
        writer.ids = new IdGenerator();
        Item item = Items.newItem(t);
        writer.write(item);
    }

    public static UriResolver mockUriResolver() {
        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getBaseUri()).thenReturn(URI.create(BASE_URI));
        return new UriResolver(uriInfo);
    }

    protected String result() {
        return out.toString().replaceAll("\n", "").replace('\"', '\'');
    }

    protected String div(String body) {
        return "<div>" + body + "</div>";
    }

    protected String ul(String cssClass, String... items) {
        String lis = "";
        for (String item : items) {
            lis += "<li>" + item + "</li>";
        }
        return "<ul class='" + cssClass + "'>" + lis + "</ul>";
    }

    protected String a(String attributes, String body) {
        return "<a " + attributes + ">" + body + "</a>";
    }

    @Data
    @AllArgsConstructor
    protected static class OneStringPojo {
        private String string;
    }

    @Data
    @AllArgsConstructor
    protected static class OneStringInputNamedPojo {
        @HtmlFieldName("foo")
        private String string;
    }

    @AllArgsConstructor
    protected static class TwoFieldPojo {
        public String str;
        public Integer i;
    }

    @Data
    @AllArgsConstructor
    @XmlRootElement
    @XmlType(propOrder = { "list", "str" })
    protected static class ListPojo {
        private String str;
        private List<String> list;
    }

    @Data
    @AllArgsConstructor
    protected static class NestedPojo {
        @HtmlLinkText
        public String str;
        @Id
        public Integer i;
    }

    @Data
    @AllArgsConstructor
    @XmlRootElement
    @XmlType(propOrder = { "nested", "str" })
    protected static class ContainerPojo {
        private String str;
        private NestedPojo nested;
    }

}
