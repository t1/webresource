package com.github.t1.webresource.codec;

import static org.mockito.Mockito.*;

import java.io.*;
import java.net.URI;
import java.util.List;

import javax.enterprise.inject.Instance;
import javax.persistence.Id;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.*;

import lombok.*;

import org.junit.Before;

public abstract class AbstractHtmlWriterTest {
    public static final String BASE_URI = "http://localhost:8080/demo/resource/";

    protected final IdGenerator ids = new IdGenerator();
    protected final UriResolver uriResolver = new UriResolver();
    protected final Writer writer = new StringWriter();
    protected final HtmlOut out = new HtmlOut();

    @Before
    public void mockHtmlOut() {
        out.setOut(writer);

        HtmlListWriter listWriter = new HtmlListWriter();
        listWriter.out = out;
        out.htmlListWriter = instance(listWriter);

        HtmlTableWriter tableWriter = new HtmlTableWriter();
        tableWriter.out = out;
        tableWriter.ids = ids;
        out.htmlTableWriter = instance(tableWriter);

        HtmlFieldWriter fieldWriter = new HtmlFieldWriter();
        fieldWriter.out = out;
        out.htmlFieldWriter = instance(fieldWriter);

        HtmlTitleWriter titleWriter = new HtmlTitleWriter();

        HtmlLinkWriter linkWriter = new HtmlLinkWriter();
        linkWriter.out = out;
        linkWriter.uriResolver = uriResolver;
        linkWriter.titleWriter = titleWriter;
        out.htmlLinkWriter = instance(linkWriter);
    }

    private <T> Instance<T> instance(T writer) {
        @SuppressWarnings("unchecked")
        Instance<T> mock = mock(Instance.class);
        when(mock.get()).thenReturn(writer);
        return mock;
    }

    @Before
    public void mockUriInfo() {
        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getBaseUri()).thenReturn(URI.create(BASE_URI));
        uriResolver.uriInfo = uriInfo;
    }

    protected String result() {
        return writer.toString().replaceAll("\n", "").replace('\"', '\'');
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
        @HtmlTitle
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
