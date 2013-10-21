package com.github.t1.webresource.codec;

import static java.util.Arrays.*;
import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;

import com.google.common.collect.ImmutableList;


public class HtmlTableWriterTest extends AbstractHtmlWriterTest {
    private void write(Object t) {
        write(HtmlTableWriter.class, t);
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

    @Test
    public void shouldEncodeListOfOneElementMapsAsTable() throws Exception {
        Map<String, String> map0 = new LinkedHashMap<>();
        map0.put("one", "111");

        Map<String, String> map1 = new LinkedHashMap<>();
        map1.put("one", "aaa");

        write(Arrays.asList(map0, map1));

        assertEquals(table("one") + tr("111") + tr("aaa") + endTable(), result());
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

        write(Arrays.asList(map0, map1));

        assertEquals(table("one", "two", "three") //
                + tr("111", "222", "333") //
                + tr("aaa", "bbb", "ccc") //
                + endTable(), result());
    }

    @Test
    public void shouldEncodeOneStringPojoListAsTable() throws Exception {
        OneStringPojo pojo1 = new OneStringPojo("one");
        OneStringPojo pojo2 = new OneStringPojo("two");
        OneStringPojo pojo3 = new OneStringPojo("three");
        List<OneStringPojo> list = asList(pojo1, pojo2, pojo3);

        write(list);

        assertEquals(table("string") + tr("one") + tr("two") + tr("three") + endTable(), result());
    }

    @Test
    public void shouldEncodeOneStringInputNamedPojoListAsTable() throws Exception {
        OneStringInputNamedPojo pojo1 = new OneStringInputNamedPojo("one");
        OneStringInputNamedPojo pojo2 = new OneStringInputNamedPojo("two");
        OneStringInputNamedPojo pojo3 = new OneStringInputNamedPojo("three");
        List<OneStringInputNamedPojo> list = asList(pojo1, pojo2, pojo3);

        write(list);

        assertEquals(table("foo") + tr("one") + tr("two") + tr("three") + endTable(), result());
    }

    @Test
    public void shouldEncodeTwoFieldPojoListAsTable() throws Exception {
        TwoFieldPojo pojo1 = new TwoFieldPojo("one", 111);
        TwoFieldPojo pojo2 = new TwoFieldPojo("two", 222);
        List<TwoFieldPojo> list = Arrays.asList(pojo1, pojo2);

        write(list);

        assertEquals(table("str", "i") //
                + tr("one", "111") //
                + tr("two", "222") //
                + endTable(), result());
    }

    @Test
    public void shouldEncodeTableWithListPojo() throws Exception {
        ListPojo pojo1 = new ListPojo("dummy1", ImmutableList.of("one1", "two1", "three1"));
        ListPojo pojo2 = new ListPojo("dummy2", ImmutableList.of("one2", "two2", "three2"));
        List<ListPojo> list = ImmutableList.of(pojo1, pojo2);

        write(list);

        assertEquals(table("list", "str") //
                + tr(ul("strings", "one1", "two1", "three1"), "dummy1") //
                + tr(ul("strings", "one2", "two2", "three2"), "dummy2") //
                + endTable(), result());
    }

    @Test
    public void shouldEncodeTableWithContainerPojo() throws Exception {
        ContainerPojo pojo1 = new ContainerPojo("dummy1", new NestedPojo("foo", 123));
        ContainerPojo pojo2 = new ContainerPojo("dummy2", new NestedPojo("bar", 321));
        List<ContainerPojo> list = ImmutableList.of(pojo1, pojo2);

        write(list);

        assertEquals(
                table("nested", "str") //
                        + tr(a("href='" + BASE_URI + "nestedpojos/123.html' id='nested-0-href' class='nestedpojos'",
                                "foo"), "dummy1") //
                        + tr(a("href='" + BASE_URI + "nestedpojos/321.html' id='nested-1-href' class='nestedpojos'",
                                "bar"), "dummy2") //
                        + endTable(), result());
    }
}
