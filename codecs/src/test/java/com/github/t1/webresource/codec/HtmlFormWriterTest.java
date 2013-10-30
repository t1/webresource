package com.github.t1.webresource.codec;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.*;

import javax.persistence.Id;

import lombok.*;

import org.junit.Test;

import com.github.t1.webresource.meta.*;
import com.google.common.collect.*;

public class HtmlFormWriterTest extends AbstractHtmlWriterTest {
    public void write(Object t) {
        HtmlFormWriter writer = new HtmlFormWriter();
        writer.ids = new IdGenerator();
        write(writer, t);
    }

    private static String wrappedForm(String type, String action, String id, String body) {
        return "<form id='" + type + "-form' action='" + action + "' method='post'>" //
                + ((id == null) ? "" : "<input name='id' type='hidden' value='" + id + "'/>") //
                + body //
                + "<input type='submit' value='submit'/></form>";
    }

    private String field(String name, String value) {
        return field(name, value, 0);
    }

    private String field(String name, String value, int id) {
        return field(name, name, value, id, "string", "text");
    }

    private String field(String type, String name, String value, int id, String cssClass, String fieldType) {
        return div(label(type, id) //
                + "<input id='" + type + "-" + id + "' name='" + name + "' class='" + cssClass + "' type='"
                + fieldType
                + "' value='" + value + "'/>" //
        );
    }

    private String label(String name) {
        return label(name, 0);
    }

    private String label(String name, int id) {
        return "<label for='" + name + "-" + id + "' class='" + name + "-label'>" + name + "</label>";
    }

    @Test
    public void shouldEncodeEmptyMap() throws Exception {
        Map<String, String> map = new LinkedHashMap<>();

        write(map);

        assertEquals(wrappedForm("linkedhashmaps", BASE_URI + "linkedhashmaps", null, ""), result());
    }

    @Test
    public void shouldEncodeOneKeyMap() throws Exception {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("one", "111");

        write(map);

        assertEquals(wrappedForm("linkedhashmaps", BASE_URI + "linkedhashmaps", null, field("one", "111")), result());
    }

    @Test
    public void shouldEncodeMap() throws Exception {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("one", "111");
        map.put("two", "222");
        map.put("three", "333");

        write(map);

        assertEquals(
                wrappedForm("linkedhashmaps", BASE_URI + "linkedhashmaps", null,
                        field("one", "111") + field("two", "222") + field("three", "333")), result());
    }

    @Test
    public void shouldEncodeOneStringPojoWithoutKey() throws Exception {
        OneStringPojo pojo = new OneStringPojo("str");

        write(pojo);

        assertEquals(wrappedForm("onestringpojos", BASE_URI + "onestringpojos", null, field("string", "str")), result());
    }

    @Test
    public void shouldEncodeOneStringPojoNullValue() throws Exception {
        OneStringPojo pojo = new OneStringPojo(null);

        Item item = Items.newItem(pojo);
        assertEquals(1, item.traits().size());
        assertEquals("string", item.trait("string").type());

        write(pojo);

        assertEquals(wrappedForm("onestringpojos", BASE_URI + "onestringpojos", null, field("string", "")), result());
    }

    @Test
    public void shouldEncodeOneStringInputNamedPojoWithoutKey() throws Exception {
        OneStringInputNamedPojo pojo = new OneStringInputNamedPojo("str");

        write(pojo);

        assertEquals(
                wrappedForm("onestringinputnamedpojos", BASE_URI + "onestringinputnamedpojos", null,
                        field("foo", "string", "str", 0, "string", "text")), result());
    }

    @Test
    public void shouldEncodeOneStringInputNamedPojoNullValue() throws Exception {
        OneStringInputNamedPojo pojo = new OneStringInputNamedPojo(null);

        write(pojo);

        assertEquals(
                wrappedForm("onestringinputnamedpojos", BASE_URI + "onestringinputnamedpojos", null,
                        field("foo", "string", "", 0, "string", "text")), result());
    }

    @Data
    @AllArgsConstructor
    private static class OneStringInputTypedPojo {
        @HtmlInputType("test")
        private String string;
    }

    @Test
    public void shouldEncodeOneStringInputTypedPojoWithoutKey() throws Exception {
        OneStringInputTypedPojo pojo = new OneStringInputTypedPojo("str");

        write(pojo);

        assertEquals(
                wrappedForm("onestringinputtypedpojos", BASE_URI + "onestringinputtypedpojos", null,
                        field("string", "string", "str", 0, "string", "test")), result());
    }

    @Test
    public void shouldEncodeTwoFieldPojoAsSequenceOfDivsWithLabelsAndReadonlyInputs() throws Exception {
        TwoFieldPojo pojo = new TwoFieldPojo("dummy", 123);

        write(pojo);

        assertThat(result(), containsString(field("i", "i", "123", 0, "number", "text")));
        assertThat(result(), containsString(field("str", "dummy")));
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

        write(pojo);

        assertThat(result(), containsString(field("b", "b", "true", 0, "boolean", "checkbox")));
        assertThat(result(), containsString(field("str", "dummy")));
    }

    @AllArgsConstructor
    static class PojoWithId {
        @Id
        private final int id;
        public String str;
    }

    @Test
    public void shouldEncodePojoWithId() throws Exception {
        PojoWithId pojo = new PojoWithId(123, "dummy");

        write(pojo);

        assertEquals(wrappedForm("pojowithids", BASE_URI + "pojowithids", "123", //
                field("str", "str", "dummy", 0, "string", "text")), result());
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

        write(pojo);

        assertThat(result(), containsString(field("str", "dummy")));
        assertThat(result(), containsString(div(label("set") + ul("strings", "one", "two", "three"))));
    }

    @Test
    public void shouldEncodeListPojo() throws Exception {
        ListPojo pojo = new ListPojo("dummy", ImmutableList.of("one", "two", "three"));

        write(pojo);

        assertThat(result(), containsString(field("str", "dummy")));
        assertThat(result(), containsString(div(label("list") + ul("strings", "one", "two", "three"))));
    }

    @Test
    public void shouldEncodeNestedPojo() throws Exception {
        ContainerPojo pojo = new ContainerPojo("dummy", new NestedPojo("foo", 123));

        write(pojo);

        assertThat(
                result(),
                containsString(div(label("nested")
                        + a("href='" + BASE_URI + "nestedpojos/123.html' id='nested-0-href' class='nestedpojos'", "foo"))));
        assertThat(result(), containsString(field("str", "dummy")));
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

        write(pojo);

        assertThat(
                result(),
                containsString(div(label("nested")
                        + a("href='" + BASE_URI
                                + "linknestedpojos/foo.html' id='nested-0-href' class='linknestedpojos'", "bar"))));
    }
}
