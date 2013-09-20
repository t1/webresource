package com.github.t1.webresource.codec;

import static org.junit.Assert.*;

import java.io.StringWriter;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.*;

import org.junit.Test;

import com.github.t1.webresource.meta.*;

public class HtmlLinkWriterTest {

    private final StringWriter out = new StringWriter();
    private final AbstractHtmlWriter context = new AbstractHtmlWriter(out, null);

    @Getter
    @AllArgsConstructor
    @ToString
    public static class SimplePojo {
        String str1;
        String str2;
    }

    @Test
    public void shouldWriteSimplePojo() throws Exception {
        SimplePojo pojo = new SimplePojo("one", "two");
        Item item = Items.newItem(pojo);
        HtmlLinkWriter writer = new HtmlLinkWriter(context, item, "id", "prefix-");

        writer.write();

        assertEquals("<a href='prefix-simplepojos/" + pojo + ".html' id='id-href' class='simplepojos'>" + pojo
                + "</a>\n", out.toString());
    }

    @Getter
    @AllArgsConstructor
    @ToString
    public static class OneLinkFieldPojo {
        @HtmlLinkText
        public String getStr1() {
            return str1;
        }

        String str1;
        String str2;
    }

    @Test
    public void shouldWriteOneFieldTextLink() throws Exception {
        OneLinkFieldPojo pojo = new OneLinkFieldPojo("one", "two");
        Item item = Items.newItem(pojo);
        HtmlLinkWriter writer = new HtmlLinkWriter(context, item, "id", "prefix-");

        writer.write();

        assertEquals("<a href='prefix-onelinkfieldpojos/" + pojo
                + ".html' id='id-href' class='onelinkfieldpojos'>one</a>\n", out.toString());
    }

    @Getter
    @AllArgsConstructor
    @ToString
    @XmlRootElement
    @HtmlLinkText("${str1}-${str2}")
    public static class TextLinkVariablePojo {
        String str1;
        String str2;
    }

    @Test
    public void shouldWriteVariableLink() throws Exception {
        TextLinkVariablePojo pojo = new TextLinkVariablePojo("one", "two");
        Item item = Items.newItem(pojo);
        HtmlLinkWriter writer = new HtmlLinkWriter(context, item, "id", "prefix-");

        writer.write();

        assertEquals("<a href='prefix-textlinkvariablepojos/" + pojo
                + ".html' id='id-href' class='textlinkvariablepojos'>one-two</a>\n", out.toString());
    }
}
