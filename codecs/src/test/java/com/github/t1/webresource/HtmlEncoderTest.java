package com.github.t1.webresource;

import static java.util.Arrays.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

import javax.xml.bind.annotation.XmlTransient;

import lombok.*;

import org.junit.Test;

public class HtmlEncoderTest {
    private static final String BASE_URI = "base";
    private final Writer out = new StringWriter();

    private HtmlEncoder writer(Object object) {
        return new HtmlEncoder(object, out, Paths.get(BASE_URI));
    }

    private static String wrapped(String string) {
        return "<html><head></head><body>" + string + "</body></html>";
    }

    private String result() {
        return out.toString().replaceAll("\n", "").replace('\"', '\'');
    }

    @Test
    public void shouldEncodeNullObject() throws Exception {
        writer(null).write();

        assertEquals(wrapped(""), result());
    }

    @Test
    public void shouldEncodePrimitiveString() throws Exception {
        writer("dummy").write();

        assertEquals(wrapped("dummy"), result());
    }

    @Test
    public void shouldEscapeString() throws Exception {
        writer("string & ampersand").write();

        assertEquals(wrapped("string &amp; ampersand"), result());
    }

    @Test
    public void shouldEncodePrimitiveInteger() throws Exception {
        writer(1234).write();

        assertEquals(wrapped("1234"), result());
    }

    @Test
    public void shouldEncodeList() throws Exception {
        writer(asList("one", "two", "three")).write();

        assertEquals(wrapped("<ul><li>one</li><li>two</li><li>three</li></ul>"), result());
    }

    @Data
    @AllArgsConstructor
    private static class OneFieldPojo {
        private String string;
    }

    @Test
    public void shouldWriteOneFieldPojoWithoutKey() throws Exception {
        OneFieldPojo pojo = new OneFieldPojo("str");

        writer(pojo).write();

        assertEquals(wrapped("str"), result());
    }

    @Test
    public void shouldWriteOneFieldPojoNullValue() throws Exception {
        OneFieldPojo pojo = new OneFieldPojo(null);

        writer(pojo).write();

        assertEquals(wrapped(""), result());
    }

    @Test
    public void shouldWriteOneFieldPojoListAsUnorderedList() throws Exception {
        OneFieldPojo pojo1 = new OneFieldPojo("one");
        OneFieldPojo pojo2 = new OneFieldPojo("two");
        OneFieldPojo pojo3 = new OneFieldPojo("three");
        List<OneFieldPojo> list = asList(pojo1, pojo2, pojo3);

        writer(list).write();

        assertEquals(wrapped("<ul><li>one</li><li>two</li><li>three</li></ul>"), result());
    }

    @Data
    @AllArgsConstructor
    private static class TwoFieldPojo {
        private String str;
        private Integer i;
    }

    @Test
    public void shouldWriteTwoFieldPojoAsSequenceOfDivsWithLabelsAndReadonlyInputs() throws Exception {
        TwoFieldPojo pojo = new TwoFieldPojo("dummy", 123);

        writer(pojo).write();

        assertEquals(wrapped("" //
                + "<div>" //
                + "<label for='str-0'>str</label>" //
                + "<input id='str-0' type='text' value='dummy' readonly/>" //
                + "</div><div>" //
                + "<label for='i-0'>i</label>" //
                + "<input id='i-0' type='text' value='123' readonly/>" //
                + "</div>"), result());
    }

    @Test
    public void shouldWriteTwoFieldPojoListAsTables() throws Exception {
        TwoFieldPojo pojo1 = new TwoFieldPojo("one", 111);
        TwoFieldPojo pojo2 = new TwoFieldPojo("two", 222);
        List<TwoFieldPojo> list = Arrays.asList(pojo1, pojo2);

        writer(list).write();

        assertEquals(wrapped("<table>" //
                + "<tr><td>str</td><td>i</td></tr>" //
                + "<tr><td>one</td><td>111</td></tr>" //
                + "<tr><td>two</td><td>222</td></tr>" //
                + "</table>"), result());
    }

    @Data
    @AllArgsConstructor
    private static class PojoWithXmlTransient {
        @XmlTransient
        private String idField;
        private String str;
        private Integer i;
    }

    @Test
    public void shouldWritePojoWithXmlTransient() throws Exception {
        PojoWithXmlTransient pojo = new PojoWithXmlTransient("id", "dummy", 123);

        writer(pojo).write();

        assertThat(result(), not(containsString("idField")));
    }

    @Test
    public void shouldWritePojoWithTransientList() throws Exception {
        PojoWithXmlTransient pojo1 = new PojoWithXmlTransient("a", "one", 111);
        PojoWithXmlTransient pojo2 = new PojoWithXmlTransient("b", "two", 222);
        List<PojoWithXmlTransient> list = Arrays.asList(pojo1, pojo2);

        writer(list).write();

        assertThat(result(), not(containsString("idField")));
    }

    @Data
    @AllArgsConstructor
    private static class PojoWithOneHtmlHead {
        @HtmlHead
        private String str;
        private Integer i;
    }

    @Test
    public void shouldWritePojoWithOneHtmlHead() throws Exception {
        PojoWithOneHtmlHead pojo = new PojoWithOneHtmlHead("dummy", 123);

        writer(pojo).write();

        assertThat(result(), containsString("<head><title>dummy</title></head>"));
    }

    @Data
    @AllArgsConstructor
    private static class PojoWithTwoHtmlHeads {
        @HtmlHead
        private String str0;
        @HtmlHead
        private String str1;
    }

    @Test
    public void shouldWritePojoWithTwoHtmlHead() throws Exception {
        PojoWithTwoHtmlHeads pojo = new PojoWithTwoHtmlHeads("dummy0", "dummy1");

        writer(pojo).write();

        assertThat(result(), containsString("<head><title>dummy0 - dummy1</title></head>"));
    }

    @Data
    @AllArgsConstructor
    @HtmlStyleSheet("/absolute")
    private static class PojoWithAbsoluteCss {
        private String str;
    }

    @Test
    public void shouldAddAbsoluteCssStyleSheet() throws Exception {
        PojoWithAbsoluteCss pojo = new PojoWithAbsoluteCss("dummy");

        writer(pojo).write();

        assertEquals("<html><head>" //
                + "<link rel='stylesheet' href='/absolute' type='text/css'/>" //
                + "</head><body>dummy</body></html>", result());
    }

    @Data
    @AllArgsConstructor
    @HtmlStyleSheet("relative")
    private static class PojoWithRelativeCss {
        private String str;
    }

    @Test
    public void shouldAddRelativeCssStyleSheet() throws Exception {
        PojoWithRelativeCss pojo = new PojoWithRelativeCss("dummy");

        writer(pojo).write();

        assertThat(result(), containsString("<head>" //
                + "<link rel='stylesheet' href='/base/relative' type='text/css'/>" //
                + "</head>"));
    }

    @Data
    @AllArgsConstructor
    @HtmlStyleSheets({ @HtmlStyleSheet("/absolute"), @HtmlStyleSheet("relative") })
    private static class PojoWithTwoCss {
        private String str;
    }

    @Test
    public void shouldAddTwoCssStyleSheets() throws Exception {
        PojoWithTwoCss pojo = new PojoWithTwoCss("dummy");

        writer(pojo).write();

        assertThat(result(), containsString("<head>" //
                + "<link rel='stylesheet' href='/absolute' type='text/css'/>" //
                + "<link rel='stylesheet' href='/base/relative' type='text/css'/>" //
                + "</head>"));
    }
}
