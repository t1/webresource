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
            public Attribute answer(InvocationOnMock invocation) {
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
    public static class TwoStringPojo {
        String str1;
        String str2;

        @Override
        public String toString() {
            return str1 + "-" + str2;
        }
    }

    @Test
    public void shouldWriteTwoStringPojo() {
        TwoStringPojo pojo = new TwoStringPojo("one", "two");

        write(pojo);

        assertEquals("<a href='twostringpojos/one-two' id='id-href' class='twostringpojos'>" + pojo + "</a>", result());
    }

    @Getter
    @AllArgsConstructor
    public static class HtmlTitlePojo {
        @HtmlTitle
        String str1;
        String str2;

        @Override
        public String toString() {
            return str1 + "-" + str2;
        }
    }

    @Test
    public void shouldWriteHtmlTitleLink() {
        HtmlTitlePojo pojo = new HtmlTitlePojo("one", "two");

        write(pojo);

        assertEquals("<a href='htmltitlepojos/one-two' id='id-href' class='htmltitlepojos'>one</a>", result());
    }

    @Getter
    @ToString
    @AllArgsConstructor
    @XmlRootElement
    public static class WebResourceKeyPojo {
        @WebResourceKey
        String str1;
    }

    @Test
    public void shouldWriteWebResourceKeyLink() {
        WebResourceKeyPojo pojo = new WebResourceKeyPojo("one");

        write(pojo);

        assertEquals("<a href='webresourcekeypojos/one' " + "id='id-href' class='webresourcekeypojos'>one</a>",
                result());
    }


    @Getter
    @AllArgsConstructor
    @XmlRootElement
    @HtmlTitle("${str1}-${str2}")
    public static class VariablePojo {
        String str1;
        String str2;

        @Override
        public String toString() {
            return str1 + "-" + str2;
        }
    }

    @Test
    public void shouldWriteVariableLink() {
        VariablePojo pojo = new VariablePojo("one", "two");

        write(pojo);

        assertEquals("<a href='variablepojos/one-two' id='id-href' class='variablepojos'>one-two</a>", result());
    }
}
