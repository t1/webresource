package com.github.t1.webresource.codec;

import static java.util.Arrays.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.*;
import java.net.URI;
import java.util.*;

import javax.xml.bind.annotation.*;

import lombok.*;

import org.junit.*;

import com.google.common.collect.*;

public class HtmlEncoderTest {
    private static final String BASE_URI = "http://localhost:8080/demo/resource";
    private final Writer out = new StringWriter();

    private HtmlEncoder writer(Object object) {
        return new HtmlEncoder(object, out, URI.create(BASE_URI));
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

        assertEquals(wrapped(ul("one", "two", "three")), result());
    }

    private String ul(String... items) {
        String lis = "";
        for (String item : items) {
            lis += "<li>" + item + "</li>";
        }
        return "<ul>" + lis + "</ul>";
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

        assertEquals(wrapped(field("one", "111") + field("two", "222") + field("three", "333")), result());
    }

    private String field(String name, String value) {
        return field(name, value, "text");
    }

    private String field(String name, String value, String type) {
        return div( //
        label(name) //
                + "<input id='" + name + "-0' class='" + name + "' type='" + type + "' value='"
                + value
                + "' readonly/>" //
        );
    }

    private String div(String body) {
        return "<div>" + body + "</div>";
    }

    private String label(String name) {
        return "<label for='" + name + "-0' class='" + name + "-label'>" + name + "</label>";
    }

    @Test
    public void shouldEncodeListOfOneElementMapsAsList() throws Exception {
        Map<String, String> map0 = new LinkedHashMap<>();
        map0.put("one", "111");

        Map<String, String> map1 = new LinkedHashMap<>();
        map1.put("one", "aaa");

        writer(Arrays.asList(map0, map1)).write();

        assertEquals(wrapped(ul("111", "aaa")), result());
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

        assertEquals(wrapped(table("one", "two", "three") //
                + tr("111", "222", "333") //
                + tr("aaa", "bbb", "ccc") //
                + endTable()), result());
    }

    private String table(String... columns) {
        String ths = "";
        for (String column : columns) {
            ths += "<th>" + column + "</th>";
        }
        return "<table><thead><tr>" + ths + "</tr></thead><tbody>";
    }

    private String endTable() {
        return "</tbody></table>";
    }

    private String tr(String... columns) {
        String tds = "";
        for (String column : columns) {
            tds += "<td>" + column + "</td>";
        }
        return "<tr>" + tds + "</tr>";
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

        assertEquals(wrapped(ul("one", "two", "three")), result());
    }

    @AllArgsConstructor
    @SuppressWarnings("unused")
    private static class TwoFieldPojo {
        public String str;
        public Integer i;
    }

    @Test
    public void shouldWriteTwoFieldPojoAsSequenceOfDivsWithLabelsAndReadonlyInputs() throws Exception {
        TwoFieldPojo pojo = new TwoFieldPojo("dummy", 123);

        writer(pojo).write();

        assertThat(result(), containsString(field("i", "123")));
        assertThat(result(), containsString(field("str", "dummy")));
    }

    @Test
    public void shouldWriteTwoFieldPojoListAsTables() throws Exception {
        TwoFieldPojo pojo1 = new TwoFieldPojo("one", 111);
        TwoFieldPojo pojo2 = new TwoFieldPojo("two", 222);
        List<TwoFieldPojo> list = Arrays.asList(pojo1, pojo2);

        writer(list).write();

        assertEquals(wrapped(table("str", "i") //
                + tr("one", "111") //
                + tr("two", "222") //
                + endTable()), result());
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

        assertThat(result(), containsString(field("b", "true", "checkbox")));
        assertThat(result(), containsString(field("str", "dummy")));
    }

    @AllArgsConstructor
    private static class PojoWithOneHtmlHead {
        @HtmlHead
        public String str;
        public Integer i;
    }

    @Test
    public void shouldWritePojoWithOneHtmlHead() throws Exception {
        PojoWithOneHtmlHead pojo = new PojoWithOneHtmlHead("dummy", 123);
        assertEquals(123, (int) pojo.i); // cover

        writer(pojo).write();

        assertThat(result(), containsString("<head><title>dummy</title></head>"));
    }

    @AllArgsConstructor
    private static class PojoWithTwoHtmlHeads {
        @HtmlHead
        public String str0;
        @HtmlHead
        public String str1;
    }

    @Test
    public void shouldWritePojoWithTwoHtmlHead() throws Exception {
        PojoWithTwoHtmlHeads pojo = new PojoWithTwoHtmlHeads("dummy0", "dummy1");

        writer(pojo).write();

        assertThat(result(), containsString("<head><title>dummy0 - dummy1</title></head>"));
    }

    @Data
    @AllArgsConstructor
    @HtmlStyleSheet("/root-path")
    private static class PojoWithRootPathCss {
        private String str;
    }

    @Test
    public void shouldLinkRootPathCssStyleSheet() throws Exception {
        PojoWithRootPathCss pojo = new PojoWithRootPathCss("dummy");

        writer(pojo).write();

        assertEquals("<html><head>" //
                + "<link rel='stylesheet' href='/root-path' type='text/css'/>" //
                + "</head><body>dummy</body></html>", result());
    }

    @Test
    public void shouldWritePojoListWithRootPathCssStyleSheet() throws Exception {
        PojoWithRootPathCss pojo1 = new PojoWithRootPathCss("a");
        PojoWithRootPathCss pojo2 = new PojoWithRootPathCss("b");
        List<PojoWithRootPathCss> list = Arrays.asList(pojo1, pojo2);

        writer(list).write();

        assertEquals("<html><head>" //
                + "<link rel='stylesheet' href='/root-path' type='text/css'/>" //
                + "</head><body>" + ul("a", "b") + "</body></html>", result());
    }

    @Data
    @AllArgsConstructor
    @HtmlStyleSheet(value = "file:src/test/resources/testfile.txt", inline = true)
    private static class PojoWithInlineFileCss {
        private String str;
    }

    @Test
    public void shouldInlineFileCssStyleSheet() throws Exception {
        PojoWithInlineFileCss pojo = new PojoWithInlineFileCss("dummy");

        writer(pojo).write();

        assertEquals("<html><head>" //
                + "<style>" //
                + "test-file-contents" //
                + "</style>" //
                + "</head><body>dummy</body></html>", result());
    }

    @Data
    @AllArgsConstructor
    @HtmlStyleSheet(value = "/stylesheets/main.css", inline = true)
    private static class PojoWithInlineRootResourceCss {
        private String str;
    }

    @Test
    @Ignore("needs a running service and I have no idea how to map that to a file-url")
    public void shouldInlineRootResourceCssStyleSheet() throws Exception {
        PojoWithInlineRootResourceCss pojo = new PojoWithInlineRootResourceCss("dummy");

        writer(pojo).write();

        assertThat(result(), containsString("<head><style>test-file-contents</style></head>"));
    }

    @Data
    @AllArgsConstructor
    @HtmlStyleSheet(value = "stylesheets/main.css", inline = true)
    private static class PojoWithInlineLocalResourceCss {
        private String str;
    }

    @Test
    @Ignore("needs a running service and I have no idea how to map that to a file-url")
    public void shouldInlineLocalResourceCssStyleSheet() throws Exception {
        PojoWithInlineLocalResourceCss pojo = new PojoWithInlineLocalResourceCss("dummy");

        writer(pojo).write();

        assertThat(result(), containsString("<head><style>test-file-contents</style></head>"));
    }

    @Data
    @AllArgsConstructor
    @HtmlStyleSheet("local-path")
    private static class PojoWithLocalCss {
        private String str;
    }

    @Test
    public void shouldLinkLocalCssStyleSheet() throws Exception {
        PojoWithLocalCss pojo = new PojoWithLocalCss("dummy");

        writer(pojo).write();

        assertThat(result(), containsString("<head>" //
                + "<link rel='stylesheet' href='/demo/local-path' type='text/css'/>" //
                + "</head>"));
    }

    @Data
    @AllArgsConstructor
    @HtmlStyleSheets({ @HtmlStyleSheet("/root-path"), @HtmlStyleSheet("local-path") })
    private static class PojoWithTwoCss {
        private String str;
    }

    @Test
    public void shouldLinkTwoCssStyleSheets() throws Exception {
        PojoWithTwoCss pojo = new PojoWithTwoCss("dummy");

        writer(pojo).write();

        assertThat(result(), containsString("<head>" //
                + "<link rel='stylesheet' href='/root-path' type='text/css'/>" //
                + "<link rel='stylesheet' href='/demo/local-path' type='text/css'/>" //
                + "</head>"));
    }

    @Data
    @AllArgsConstructor
    private static class SetPojo {
        private String str;
        private Set<String> set;
    }

    @Test
    public void shouldWriteSetPojo() throws Exception {
        SetPojo pojo = new SetPojo("dummy", ImmutableSet.of("one", "two", "three"));

        writer(pojo).write();

        assertThat(result(), containsString(field("str", "dummy")));
        assertThat(result(), containsString(field("set", "[one, two, three]")));
    }

    @Data
    @AllArgsConstructor
    @XmlRootElement
    @XmlType(propOrder = { "list", "str" })
    private static class ListPojo {
        private String str;
        private List<String> list;
    }

    @Test
    public void shouldWriteListPojo() throws Exception {
        ListPojo pojo = new ListPojo("dummy", ImmutableList.of("one", "two", "three"));

        writer(pojo).write();

        assertThat(result(), containsString(field("str", "dummy")));
        assertThat(result(), containsString(div(label("list") + ul("one", "two", "three"))));
    }

    @Test
    public void shouldWriteTableWithListPojo() throws Exception {
        ListPojo pojo1 = new ListPojo("dummy1", ImmutableList.of("one1", "two1", "three1"));
        ListPojo pojo2 = new ListPojo("dummy2", ImmutableList.of("one2", "two2", "three2"));
        List<ListPojo> list = ImmutableList.of(pojo1, pojo2);

        writer(list).write();

        assertEquals(wrapped(table("list", "str") //
                + tr(ul("one1", "two1", "three1"), "dummy1") //
                + tr(ul("one2", "two2", "three2"), "dummy2") //
                + endTable()), result());
    }

    @Data
    @AllArgsConstructor
    private static class NestedPojo {
        public String str;
        public Integer i;
    }

    @Data
    @AllArgsConstructor
    @XmlRootElement
    @XmlType(propOrder = { "nested", "str" })
    private static class ContainerPojo {
        private String str;
        private NestedPojo nested;
    }

    @Test
    public void shouldWriteNestedPojo() throws Exception {
        ContainerPojo pojo = new ContainerPojo("dummy", new NestedPojo("foo", 123));

        writer(pojo).write();

        assertThat(result(), containsString(field("str", "dummy")));
        assertThat(result(), containsString(div(//
                label("nested") + div(field("str", "foo") + field("i", "123")))));
    }

    @Test
    public void shouldWriteTableWithContainerPojo() throws Exception {
        ContainerPojo pojo1 = new ContainerPojo("dummy1", new NestedPojo("foo", 123));
        ContainerPojo pojo2 = new ContainerPojo("dummy2", new NestedPojo("bar", 321));
        List<ContainerPojo> list = ImmutableList.of(pojo1, pojo2);

        writer(list).write();

        assertEquals(
                wrapped(table("nested", "str")
                        + tr(div(div(label("str") + "<input id='str-0' class='str' type='text' value='foo' readonly/>")
                                + div(label("i") + "<input id='i-0' class='i' type='text' value='123' readonly/>")),
                                "dummy1")
                        + tr(div(div(label("str") + "<input id='str-0' class='str' type='text' value='bar' readonly/>")
                                + div(label("i") + "<input id='i-0' class='i' type='text' value='321' readonly/>")),
                                "dummy2") //
                        + endTable()), result());
    }
}
