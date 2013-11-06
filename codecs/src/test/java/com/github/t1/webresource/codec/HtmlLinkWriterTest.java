package com.github.t1.webresource.codec;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.*;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.github.t1.webresource.WebResourceKey;
import com.github.t1.webresource.codec.HtmlOut.Attribute;
import com.github.t1.webresource.meta.*;

public class HtmlLinkWriterTest extends AbstractHtmlWriterTest {
    private void write(Object pojo) {
        HrefAttribute href = mock(HrefAttribute.class);
        when(href.to(any(Item.class))).thenAnswer(new Answer<Attribute>() {
            @Override
            public Attribute answer(InvocationOnMock invocation) throws Throwable {
                Item item = (Item) invocation.getArguments()[0];
                return new Attribute("href", item.type() + "/" + HtmlId.of(item));
            }
        });

        HtmlLinkWriter writer = new HtmlLinkWriter();
        writer.out = out;
        writer.href = href;
        writer.titleWriter = new HtmlTitleWriter();
        writer.write(Items.newItem(pojo), "id");
    }

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

        write(pojo);

        assertEquals("<a href='simplepojos/one-two' id='id-href' class='simplepojos'>" + pojo + "</a>", result());
    }

    @Getter
    @AllArgsConstructor
    public static class OneLinkFieldPojo {
        @HtmlTitle
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

        write(pojo);

        assertEquals("<a href='onelinkfieldpojos/one-two' id='id-href' class='onelinkfieldpojos'>one</a>", result());
    }

    @Getter
    @ToString
    @AllArgsConstructor
    @XmlRootElement
    public static class TextLinkWebResourceKeyPojo {
        @WebResourceKey
        String str1;
    }

    @Test
    public void shouldWriteWebResourceKeyLink() throws Exception {
        TextLinkWebResourceKeyPojo pojo = new TextLinkWebResourceKeyPojo("one");

        write(pojo);

        assertEquals("<a href='textlinkwebresourcekeypojos/one' "
                + "id='id-href' class='textlinkwebresourcekeypojos'>one</a>", result());
    }


    @Getter
    @AllArgsConstructor
    @XmlRootElement
    @HtmlTitle("${str1}-${str2}")
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

        write(pojo);

        assertEquals("<a href='textlinkvariablepojos/one-two' id='id-href' class='textlinkvariablepojos'>one-two</a>",
                result());
    }
}
