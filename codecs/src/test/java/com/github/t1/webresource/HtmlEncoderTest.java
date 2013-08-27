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

import com.google.common.collect.ImmutableList;

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

    @Test
    public void shouldEncodeEmptyMap() throws Exception {
        Map<String, String> map = new LinkedHashMap<>();

        writer(map).write();

        assertEquals(wrapped(""), result());
    }

    @Test
    public void shouldEncodeOneKeyMap() throws Exception {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("one", "111");

        writer(map).write();

        assertEquals(wrapped("111"), result());
    }

    @Test
    public void shouldEncodeMap() throws Exception {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("one", "111");
        map.put("two", "222");
        map.put("three", "333");

        writer(map).write();

        assertEquals(wrapped(div("one-0", "one", "111") + div("two-0", "two", "222") + div("three-0", "three", "333")),
                result());
    }

    private String div(String id, String name, String value) {
        return div(id, name, value, "text");
    }

    private String div(String id, String name, String value, String type) {
        return "<div>" //
                + "<label for='" + id + "'>" + name + "</label>" //
                + "<input id='" + id + "' type='" + type + "' value='" + value + "' readonly/>" //
                + "</div>";
    }

    @Test
    public void shouldEncodeListOfOneElementMapsAsList() throws Exception {
        Map<String, String> map0 = new LinkedHashMap<>();
        map0.put("one", "111");

        Map<String, String> map1 = new LinkedHashMap<>();
        map1.put("one", "aaa");

        writer(Arrays.asList(map0, map1)).write();

        assertEquals(wrapped("<ul><li>111</li><li>aaa</li></ul>"), result());
    }

    @Test
    public void shouldEncodeListOfMapsAsTable() throws Exception {
        Map<String, String> map0 = new LinkedHashMap<>();
        map0.put("one", "111");
        map0.put("two", "222");
        map0.put("three", "333");

        Map<String, String> map1 = new LinkedHashMap<>();
        map1.put("one", "aaa");
        map1.put("two", "bbb");
        map1.put("three", "ccc");

        writer(Arrays.asList(map0, map1)).write();

        assertEquals(wrapped("<table>" //
                + "<thead>" //
                + "<tr><th>one</th><th>two</th><th>three</th></tr>" //
                + "</thead><tbody>" //
                + "<tr><td>111</td><td>222</td><td>333</td></tr>" //
                + "<tr><td>aaa</td><td>bbb</td><td>ccc</td></tr>" //
                + "</tbody>" //
                + "</table>"), result());
    }

    @Data
    @AllArgsConstructor
    private static class OneStringPojo {
        private String string;
    }

    @Test
    public void shouldWriteOneStringPojoWithoutKey() throws Exception {
        OneStringPojo pojo = new OneStringPojo("str");

        writer(pojo).write();

        assertEquals(wrapped("str"), result());
    }

    @Test
    public void shouldWriteOneStringPojoNullValue() throws Exception {
        OneStringPojo pojo = new OneStringPojo(null);

        writer(pojo).write();

        assertEquals(wrapped(""), result());
    }

    @Test
    public void shouldWriteOneStringPojoListAsUnorderedList() throws Exception {
        OneStringPojo pojo1 = new OneStringPojo("one");
        OneStringPojo pojo2 = new OneStringPojo("two");
        OneStringPojo pojo3 = new OneStringPojo("three");
        List<OneStringPojo> list = asList(pojo1, pojo2, pojo3);

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

        assertEquals(wrapped(div("str-0", "str", "dummy") + div("i-0", "i", "123")), result());
    }

    @Test
    public void shouldWriteTwoFieldPojoListAsTables() throws Exception {
        TwoFieldPojo pojo1 = new TwoFieldPojo("one", 111);
        TwoFieldPojo pojo2 = new TwoFieldPojo("two", 222);
        List<TwoFieldPojo> list = Arrays.asList(pojo1, pojo2);

        writer(list).write();

        assertEquals(wrapped("<table>" //
                + "<thead>" //
                + "<tr><th>str</th><th>i</th></tr>" //
                + "</thead><tbody>" //
                + "<tr><td>one</td><td>111</td></tr>" //
                + "<tr><td>two</td><td>222</td></tr>" //
                + "</tbody>" //
                + "</table>"), result());
    }

    @Data
    @AllArgsConstructor
    private static class TwoFieldsOneBooleanPojo {
        private boolean b;
        private String str;
    }

    @Test
    public void shouldWriteOneBooleanPojoWithoutKey() throws Exception {
        TwoFieldsOneBooleanPojo pojo = new TwoFieldsOneBooleanPojo(true, "dummy");

        writer(pojo).write();

        assertEquals(wrapped(div("b-0", "b", "true", "checkbox") + div("str-0", "str", "dummy")), result());
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

    @Test
    public void shouldWritePojoListWithAbsoluteCssStyleSheet() throws Exception {
        PojoWithAbsoluteCss pojo1 = new PojoWithAbsoluteCss("a");
        PojoWithAbsoluteCss pojo2 = new PojoWithAbsoluteCss("b");
        List<PojoWithAbsoluteCss> list = Arrays.asList(pojo1, pojo2);

        writer(list).write();

        assertEquals("<html><head>" //
                + "<link rel='stylesheet' href='/absolute' type='text/css'/>" //
                + "</head><body><ul><li>a</li><li>b</li></ul></body></html>", result());
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

    @Data
    @AllArgsConstructor
    private static class NestedListPojo {
        private String str;
        private List<String> list;
    }

    @Test
    public void shouldWriteNestedListPojo() throws Exception {
        NestedListPojo pojo = new NestedListPojo("dummy", ImmutableList.of("one", "two", "three"));

        writer(pojo).write();

        assertEquals(wrapped(div("str-0", "str", "dummy") + div("list-0", "list", "[one, two, three]")), result());
    }
}
