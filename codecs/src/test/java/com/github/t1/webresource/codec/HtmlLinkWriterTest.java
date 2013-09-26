package com.github.t1.webresource.codec;

import static org.junit.Assert.*;

import java.io.StringWriter;
import java.net.URI;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.*;

import org.junit.Test;

import com.github.t1.webresource.meta.*;

public class HtmlLinkWriterTest {
    private static final URI BASE_URI = URI.create("http://host/app/rest/");
    private final StringWriter out = new StringWriter();
    private final AbstractHtmlWriter context = new AbstractHtmlWriter(out, BASE_URI);

    @Getter
    @AllArgsConstructor
    public static class SimplePojo {
        String str1;
        String str2;

        @Override
        public String toString() {
            return str1 + "-" + str2;
        }
    }

    @Test
    public void shouldWriteSimplePojo() throws Exception {
        SimplePojo pojo = new SimplePojo("one", "two");
        Item item = Items.newItem(pojo);
        HtmlLinkWriter writer = new HtmlLinkWriter(context, item, "id");

        writer.write();

        assertEquals("<a href='" + BASE_URI + "simplepojos/" + pojo + ".html' id='id-href' class='simplepojos'>" + pojo
                + "</a>\n", out.toString());
    }

    @Getter
    @AllArgsConstructor
    public static class OneLinkFieldPojo {
        @HtmlLinkText
        String str1;
        String str2;

        @Override
        public String toString() {
            return str1 + "-" + str2;
        }
    }

    @Test
    public void shouldWriteOneFieldTextLink() throws Exception {
        OneLinkFieldPojo pojo = new OneLinkFieldPojo("one", "two");
        Item item = Items.newItem(pojo);
        HtmlLinkWriter writer = new HtmlLinkWriter(context, item, "id");

        writer.write();

        assertEquals("<a href='" + BASE_URI + "onelinkfieldpojos/" + pojo
                + ".html' id='id-href' class='onelinkfieldpojos'>one</a>\n", out.toString());
    }

    @Getter
    @AllArgsConstructor
    @XmlRootElement
    @HtmlLinkText("${str1}-${str2}")
    public static class TextLinkVariablePojo {
        String str1;
        String str2;

        @Override
        public String toString() {
            return str1 + "-" + str2;
        }
    }

    @Test
    public void shouldWriteVariableLink() throws Exception {
        TextLinkVariablePojo pojo = new TextLinkVariablePojo("one", "two");
        Item item = Items.newItem(pojo);
        HtmlLinkWriter writer = new HtmlLinkWriter(context, item, "id");

        writer.write();

        assertEquals("<a href='" + BASE_URI + "textlinkvariablepojos/" + pojo
                + ".html' id='id-href' class='textlinkvariablepojos'>one-two</a>\n", out.toString());
    }
}
