package com.github.t1.webresource.codec;

import static java.util.Arrays.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.*;

import javax.persistence.Id;
import javax.xml.bind.annotation.*;

import lombok.*;

import org.junit.Test;

import com.github.t1.webresource.meta.*;

public class HtmlFormWriterTest extends AbstractHtmlWriterTest {
    HtmlFormWriter writer = new HtmlFormWriter();

    public void write(Object t) {
        writer.out = out;
        writer.ids = ids;
        writer.uriResolver = uriResolver;
        writer.fieldWriter = mock(HtmlFieldWriter.class);
        doAnswer(writeDummyAnswer("field")).when(writer.fieldWriter).write(any(Item.class), any(Trait.class),
                anyString());
        writer.write(Items.newItem(t));
    }

    private void mockListWriter() {
        writer.listWriter = mock(HtmlListWriter.class);
        doAnswer(writeDummyAnswer("list")).when(writer.listWriter).write(any(Item.class));
    }

    private static String form(String type, String body) {
        return startForm(type) + body + endForm();
    }

    private static String startForm(String type) {
        return "<form id='" + type + "-form' action='" + BASE_URI + type + "' method='post'>";
    }

    private static String endForm() {
        return "<input type='submit' value='submit'/></form>";
    }

    private String field(String type) {
        return div(label(type) + "{field}");
    }

    private String list(String type) {
        return div(label(type) + "{list}");
    }

    private String label(String name) {
        return "<label for='" + name + "-0' class='" + name + "-label'>" + name + "</label>";
    }

    @Test
    public void shouldEncodeEmptyMap() {
        Map<String, String> map = new LinkedHashMap<>();

        write(map);

        assertEquals(form("linkedhashmaps", ""), result());
    }

    @Test
    public void shouldEncodeOneKeyMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("one", "111");

        write(map);

        assertEquals(form("linkedhashmaps", field("one")), result());
    }

    @Test
    public void shouldEncodeMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("one", "111");
        map.put("two", "222");
        map.put("three", "333");

        write(map);

        assertEquals(form("linkedhashmaps", field("one") + field("two") + field("three")), result());
    }

    @Test
    public void shouldEncodeOneStringPojoWithoutKey() {
        OneStringPojo pojo = new OneStringPojo("str");

        write(pojo);

        assertEquals(form("onestringpojos", field("string")), result());
    }

    @Test
    public void shouldEncodeOneStringPojoNullValue() {
        OneStringPojo pojo = new OneStringPojo(null);

        Item item = Items.newItem(pojo);
        assertEquals(1, item.traits().size());
        assertEquals("string", item.trait("string").type());

        write(pojo);

        assertEquals(form("onestringpojos", field("string")), result());
    }

    @Test
    public void shouldEncodeOneStringInputNamedPojoWithoutKey() {
        OneStringInputNamedPojo pojo = new OneStringInputNamedPojo("str");

        write(pojo);

        assertEquals(form("onestringinputnamedpojos", field("foo")), result());
    }

    @Test
    public void shouldEncodeOneStringInputNamedPojoNullValue() {
        OneStringInputNamedPojo pojo = new OneStringInputNamedPojo(null);

        write(pojo);

        assertEquals(form("onestringinputnamedpojos", field("foo")), result());
    }

    @Data
    @AllArgsConstructor
    private static class OneStringInputTypedPojo {
        @HtmlInputType("test")
        private String string;
    }

    @Test
    public void shouldEncodeOneStringInputTypedPojoWithoutKey() {
        OneStringInputTypedPojo pojo = new OneStringInputTypedPojo("str");

        write(pojo);

        assertEquals(form("onestringinputtypedpojos", field("string")), result());
    }

    @Test
    public void shouldEncodeTwoFieldPojoAsSequenceOfDivsWithLabelsAndReadonlyInputs() {
        TwoFieldPojo pojo = new TwoFieldPojo("dummy", 123);

        write(pojo);

        assertEquals(form("twofieldpojos", field("str") + field("i")), result());
    }

    @Data
    @AllArgsConstructor
    @XmlRootElement
    @XmlType(propOrder = { "b", "str" })
    private static class TwoFieldsOneBooleanPojo {
        private boolean b;
        private String str;
    }

    @Test
    public void shouldEncodeOneBooleanPojoWithoutKey() {
        TwoFieldsOneBooleanPojo pojo = new TwoFieldsOneBooleanPojo(true, "dummy");

        write(pojo);

        assertEquals(form("twofieldsonebooleanpojos", field("b") + field("str")), result());
    }

    @AllArgsConstructor
    static class PojoWithId {
        @Id
        private final int id;
        public String str;
    }

    @Test
    public void shouldEncodePojoWithId() {
        PojoWithId pojo = new PojoWithId(123, "dummy");

        write(pojo);

        assertEquals(startForm("pojowithids") //
                + "<input name='id' type='hidden' value='123'/>" //
                + field("str") + endForm(), result());
    }

    @Data
    @AllArgsConstructor
    @XmlRootElement
    @XmlType(propOrder = { "str", "set" })
    private static class SetPojo {
        private String str;
        private Set<String> set;
    }

    @Test
    public void shouldEncodeSetPojo() {
        mockListWriter();
        SetPojo pojo = new SetPojo("dummy", new LinkedHashSet<>(asList("one", "two", "three")));

        write(pojo);

        assertEquals(form("setpojos", field("str") + list("set")), result());
        verify(writer.listWriter).write(captor.capture());
        assertEqualsListItem(captor.getValue(), "one", "two", "three");
    }

    @Test
    public void shouldEncodeListPojo() {
        mockListWriter();
        ListPojo pojo = new ListPojo("dummy", asList("one", "two", "three"));

        write(pojo);

        assertEquals(form("listpojos", field("str") + list("list")), result());
        verify(writer.listWriter).write(captor.capture());
        assertEqualsListItem(captor.getValue(), "one", "two", "three");
    }

    @Test
    public void shouldEncodeNestedPojo() {
        writer.linkWriter = mock(HtmlLinkWriter.class);
        doAnswer(writeDummyAnswer("link")).when(writer.linkWriter).write(any(Item.class), anyString());
        ContainerPojo pojo = new ContainerPojo("dummy", new NestedPojo("foo", 123));

        write(pojo);

        assertEquals(form("containerpojos", field("str") + div(label("nested") + "{link}")), result());
    }

    @Data
    @AllArgsConstructor
    private static class LinkNestedPojo {
        @Id
        public String ref;
        @HtmlTitle
        public String body;
    }

    @Data
    @AllArgsConstructor
    private static class LinkContainerPojo {
        private LinkNestedPojo nested;
    }

    @Test
    public void shouldEncodeLinkNestedPojo() {
        writer.linkWriter = mock(HtmlLinkWriter.class);
        doAnswer(writeDummyAnswer("link")).when(writer.linkWriter).write(any(Item.class), anyString());
        LinkContainerPojo pojo = new LinkContainerPojo(new LinkNestedPojo("foo", "bar"));

        write(pojo);

        assertEquals(form("linkcontainerpojos", div(label("nested") + "{link}")), result());
    }
}
