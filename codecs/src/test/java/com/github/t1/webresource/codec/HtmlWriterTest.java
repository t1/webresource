package com.github.t1.webresource.codec;

import static java.util.Arrays.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.*;
import java.net.URI;
import java.util.*;

import javax.persistence.Id;
import javax.xml.bind.annotation.*;

import lombok.*;

import org.junit.Test;

import com.google.common.collect.*;

public class HtmlWriterTest {
    private static final String BASE_URI = "http://localhost:8080/demo/resource/";
    private final Writer out = new StringWriter();

    private HtmlWriter writer(Object object) {
        return new HtmlWriter(object, out, URI.create(BASE_URI));
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

        assertEquals(wrapped(field("one", "111")), result());
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
        return field(name, value, 0);
    }

    private String field(String name, String value, int id) {
        return field(name, value, id, "string", "text");
    }

    private String field(String name, String value, int id, String cssClass, String type) {
        return div(label(name, id) //
                + "<input id='" + name + "-" + id + "' class='" + cssClass + "' type='" + type + "' value='"
                + value
                + "' readonly/>" //
        );
    }

    private String div(String body) {
        return "<div>" + body + "</div>";
    }

    private String label(String name) {
        return label(name, 0);
    }

    private String label(String name, int id) {
        return "<label for='" + name + "-" + id + "' class='" + name + "-label'>" + name + "</label>";
    }

    private String a(String attributes, String body) {
        return "<a " + attributes + ">" + body + "</a>";
    }

    @Test
    public void shouldEncodeListOfOneElementMapsAsTable() throws Exception {
        Map<String, String> map0 = new LinkedHashMap<>();
        map0.put("one", "111");

        Map<String, String> map1 = new LinkedHashMap<>();
        map1.put("one", "aaa");

        writer(Arrays.asList(map0, map1)).write();

        assertEquals(wrapped(table("one") + tr("111") + tr("aaa") + endTable()), result());
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
    public void shouldEncodeOneStringPojoWithoutKey() throws Exception {
        OneStringPojo pojo = new OneStringPojo("str");

        writer(pojo).write();

        assertEquals(wrapped(field("string", "str")), result());
    }

    @Test
    public void shouldEncodeOneStringPojoNullValue() throws Exception {
        OneStringPojo pojo = new OneStringPojo(null);

        writer(pojo).write();

        assertEquals(wrapped(field("string", "")), result());
    }

    @Test
    public void shouldEncodeOneStringPojoListAsTable() throws Exception {
        OneStringPojo pojo1 = new OneStringPojo("one");
        OneStringPojo pojo2 = new OneStringPojo("two");
        OneStringPojo pojo3 = new OneStringPojo("three");
        List<OneStringPojo> list = asList(pojo1, pojo2, pojo3);

        writer(list).write();

        assertEquals(wrapped(table("string") + tr("one") + tr("two") + tr("three") + endTable()), result());
    }

    @Data
    @AllArgsConstructor
    private static class OneStringInputNamedPojo {
        private String string;

        @HtmlFieldName("foo")
        public String getString() {
            return string;
        }
    }

    @Test
    public void shouldEncodeOneStringInputNamedPojoWithoutKey() throws Exception {
        OneStringInputNamedPojo pojo = new OneStringInputNamedPojo("str");

        writer(pojo).write();

        assertEquals(wrapped(field("foo", "str", 0, "string", "text")), result());
    }

    @Test
    public void shouldEncodeOneStringInputNamedPojoNullValue() throws Exception {
        OneStringInputNamedPojo pojo = new OneStringInputNamedPojo(null);

        writer(pojo).write();

        assertEquals(wrapped(field("foo", "", 0, "string", "text")), result());
    }

    @Test
    public void shouldEncodeOneStringInputNamedPojoListAsTable() throws Exception {
        OneStringInputNamedPojo pojo1 = new OneStringInputNamedPojo("one");
        OneStringInputNamedPojo pojo2 = new OneStringInputNamedPojo("two");
        OneStringInputNamedPojo pojo3 = new OneStringInputNamedPojo("three");
        List<OneStringInputNamedPojo> list = asList(pojo1, pojo2, pojo3);

        writer(list).write();

        assertEquals(wrapped(table("foo") + tr("one") + tr("two") + tr("three") + endTable()), result());
    }

    @Data
    @AllArgsConstructor
    private static class OneStringInputTypedPojo {
        private String string;

        @HtmlInputType("test")
        public String getString() {
            return string;
        }
    }

    @Test
    public void shouldEncodeOneStringInputTypedPojoWithoutKey() throws Exception {
        OneStringInputTypedPojo pojo = new OneStringInputTypedPojo("str");

        writer(pojo).write();

        assertEquals(wrapped(field("string", "str", 0, "string", "test")), result());
    }

    @AllArgsConstructor
    @SuppressWarnings("unused")
    private static class TwoFieldPojo {
        public String str;
        public Integer i;
    }

    @Test
    public void shouldEncodeTwoFieldPojoAsSequenceOfDivsWithLabelsAndReadonlyInputs() throws Exception {
        TwoFieldPojo pojo = new TwoFieldPojo("dummy", 123);

        writer(pojo).write();

        assertThat(result(), containsString(field("i", "123", 0, "number", "text")));
        assertThat(result(), containsString(field("str", "dummy")));
    }

    @Test
    public void shouldEncodeTwoFieldPojoListAsTable() throws Exception {
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
    public void shouldEncodeOneBooleanPojoWithoutKey() throws Exception {
        TwoFieldsOneBooleanPojo pojo = new TwoFieldsOneBooleanPojo(true, "dummy");

        writer(pojo).write();

        assertThat(result(), containsString(field("b", "true", 0, "boolean", "checkbox")));
        assertThat(result(), containsString(field("str", "dummy")));
    }

    @Data
    @AllArgsConstructor
    private static class SetPojo {
        private String str;
        private Set<String> set;
    }

    @Test
    public void shouldEncodeSetPojo() throws Exception {
        SetPojo pojo = new SetPojo("dummy", ImmutableSet.of("one", "two", "three"));

        writer(pojo).write();

        assertThat(result(), containsString(field("str", "dummy")));
        assertThat(result(), containsString(div(label("set", 0) + ul("one", "two", "three"))));
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
    public void shouldEncodeListPojo() throws Exception {
        ListPojo pojo = new ListPojo("dummy", ImmutableList.of("one", "two", "three"));

        writer(pojo).write();

        assertThat(result(), containsString(field("str", "dummy")));
        assertThat(result(), containsString(div(label("list") + ul("one", "two", "three"))));
    }

    @Test
    public void shouldEncodeTableWithListPojo() throws Exception {
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
        @HtmlLinkText
        public String str;
        @Id
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
    public void shouldEncodeNestedPojo() throws Exception {
        ContainerPojo pojo = new ContainerPojo("dummy", new NestedPojo("foo", 123));

        writer(pojo).write();

        assertThat(
                result(),
                containsString(div(label("nested")
                        + a("href='" + BASE_URI + "nestedpojos/123.html' id='nested-0-href' class='nestedpojos'", "foo"))));
        assertThat(result(), containsString(field("str", "dummy")));
    }

    @Test
    public void shouldEncodeTableWithContainerPojo() throws Exception {
        ContainerPojo pojo1 = new ContainerPojo("dummy1", new NestedPojo("foo", 123));
        ContainerPojo pojo2 = new ContainerPojo("dummy2", new NestedPojo("bar", 321));
        List<ContainerPojo> list = ImmutableList.of(pojo1, pojo2);

        writer(list).write();

        assertEquals(
                wrapped(table("nested", "str") //
                        + tr(a("href='" + BASE_URI + "nestedpojos/123.html' id='nested-0-href' class='nestedpojos'",
                                "foo"), "dummy1") //
                        + tr(a("href='" + BASE_URI + "nestedpojos/321.html' id='nested-1-href' class='nestedpojos'",
                                "bar"), "dummy2") //
                        + endTable()), result());
    }

    @Data
    @AllArgsConstructor
    private static class LinkNestedPojo {
        @Id
        public String ref;
        @HtmlLinkText
        public String body;
    }

    @Data
    @AllArgsConstructor
    private static class LinkContainerPojo {
        private LinkNestedPojo nested;
    }

    @Test
    public void shouldEncodeLinkNestedPojo() throws Exception {
        LinkContainerPojo pojo = new LinkContainerPojo(new LinkNestedPojo("foo", "bar"));

        writer(pojo).write();

        assertThat(
                result(),
                containsString(div(label("nested")
                        + a("href='" + BASE_URI
                                + "linknestedpojos/foo.html' id='nested-0-href' class='linknestedpojos'", "bar"))));
    }
}
