package com.github.t1.webresource.codec;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
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
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.github.t1.webresource.meta.Item;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractHtmlWriterTest {
    public static <T> Instance<T> instance(T instance) {
        @SuppressWarnings("unchecked")
        Instance<T> mock = mock(Instance.class);
        when(mock.get()).thenReturn(instance);
        return mock;
    }

    public static final String BASE_URI = "http://localhost:8080/demo/resource/";

    protected final IdGenerator ids = new IdGenerator();
    protected final UriResolver uriResolver = new UriResolver();
    protected final Writer writer = new StringWriter();
    protected final HtmlOut out = new HtmlOut();

    @Captor
    protected ArgumentCaptor<Item> captor;

    @Before
    public void mockHtmlOut() {
        out.setOut(writer);

        HtmlTableWriter tableWriter = new HtmlTableWriter();
        tableWriter.out = out;
        tableWriter.ids = ids;
        out.htmlTableWriter = instance(tableWriter);

        HtmlFieldWriter fieldWriter = new HtmlFieldWriter();
        fieldWriter.out = out;
        out.htmlFieldWriter = instance(fieldWriter);

        HtmlLinkWriter linkWriter = mock(HtmlLinkWriter.class);
        doAnswer(writeAnswer("link")).when(linkWriter).write(any(Item.class), anyString());
        out.htmlLinkWriter = instance(linkWriter);
    }

    @Before
    public void mockUriInfo() {
        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getBaseUri()).thenReturn(URI.create(BASE_URI));
        uriResolver.uriInfo = uriInfo;
    }

    protected Answer<Void> writeAnswer(final String prefix) {
        return new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                out.write("{" + prefix + ":" + invocation.getArguments()[0] + "}");
                return null;
            }
        };
    }

    protected String result() {
        return writer.toString().replaceAll("\n", "").replace('\"', '\'');
    }

    protected static void assertEqualsListItem(Item item, String... values) {
        List<Item> list = item.getList();
        assertEquals("list item size", values.length, list.size());
        for (int i = 0; i < values.length; i++) {
            assertEquals("item " + i + " of list item", values[i], list.get(i).toString());
        }
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

    public static class NoTraitPojo {
        @Override
        public String toString() {
            return "the-pojo";
        }
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
