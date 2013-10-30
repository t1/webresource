package com.github.t1.webresource.codec;

import static org.mockito.Mockito.*;

import java.io.*;
import java.lang.reflect.*;
import java.net.URI;
import java.util.List;

import javax.enterprise.inject.Instance;
import javax.persistence.Id;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.*;

import lombok.*;

import com.github.t1.webresource.meta.*;

abstract class AbstractHtmlWriterTest {
    public static final String BASE_URI = "http://localhost:8080/demo/resource/";

    protected final Writer out = new StringWriter();

    private final UriResolver uriResolver = mockUriResolver();

    private UriResolver mockUriResolver() {
        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getBaseUri()).thenReturn(URI.create(BASE_URI));
        UriResolver uriResolver = new UriResolver();
        uriResolver.uriInfo = uriInfo;
        return uriResolver;
    }


    public void write(AbstractHtmlWriter writer, Object t) {
        init(writer);
        Item item = Items.newItem(t);
        write(writer, item);
    }

    private void init(AbstractHtmlWriter writer) {
        instance(writer);
        writer.htmlListWriter = instance(new HtmlListWriter());
        writer.htmlTableWriter = instance(new HtmlTableWriter());
        writer.htmlFieldWriter = instance(new HtmlFieldWriter());
        writer.htmlLinkWriter = instance(new HtmlLinkWriter());
    }

    private <T extends AbstractHtmlWriter> Instance<T> instance(T writer) {
        writer.out = out;
        initUriResolver(writer);
        @SuppressWarnings("unchecked")
        Instance<T> mock = mock(Instance.class);
        when(mock.get()).thenReturn(writer);
        return mock;
    }

    public void initUriResolver(AbstractHtmlWriter writer) {
        try {
            Field field = writer.getClass().getDeclaredField("uriResolver");
            field.setAccessible(true);
            field.set(writer, uriResolver);
        } catch (NoSuchFieldException e) {
            return; // nothing to init
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    protected void write(AbstractHtmlWriter writer, Item item) {
        try {
            Method method = writer.getClass().getMethod("write", Item.class);
            method.invoke(writer, item);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
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
