package com.github.t1.webresource.codec;

import static java.util.Arrays.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.Test;

import com.github.t1.webresource.meta.*;

public class HtmlTableWriterTest extends AbstractHtmlWriterTest {
    HtmlTableWriter writer = new HtmlTableWriter();

    private void write(Object t) {
        writer.ids = ids;
        writer.out = out;
        writer.fieldWriter = mock(HtmlFieldWriter.class);
        doAnswer(writeAnswer("field")).when(writer.fieldWriter).write(any(Item.class), any(Trait.class), anyString());
        writer.write(Items.newItem(t));
    }

    private String startTable(String... columns) {
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
            tds += "<td>{field:" + column + "}</td>";
        }
        return "<tr>" + tds + "</tr>";
    }

    @Test
    public void shouldEncodeListOfOneElementMapsAsTable() {
        Map<String, String> map0 = new LinkedHashMap<>();
        map0.put("one", "111");

        Map<String, String> map1 = new LinkedHashMap<>();
        map1.put("one", "aaa");

        write(Arrays.asList(map0, map1));

        assertEquals(startTable("one") + tr("111") + tr("aaa") + endTable(), result());
    }

    @Test
    public void shouldEncodeListOfMapsAsTable() {
        Map<String, String> map0 = new LinkedHashMap<>();
        map0.put("one", "111");
        map0.put("two", "222");
        map0.put("three", "333");

        Map<String, String> map1 = new LinkedHashMap<>();
        map1.put("one", "aaa");
        map1.put("two", "bbb");
        map1.put("three", "ccc");

        write(Arrays.asList(map0, map1));

        assertEquals(startTable("one", "two", "three") //
                + tr("111", "222", "333") //
                + tr("aaa", "bbb", "ccc") //
                + endTable(), result());
    }

    @Test
    public void shouldEncodeOneStringPojoListAsTable() {
        OneStringPojo pojo1 = new OneStringPojo("one");
        OneStringPojo pojo2 = new OneStringPojo("two");
        OneStringPojo pojo3 = new OneStringPojo("three");
        List<OneStringPojo> list = asList(pojo1, pojo2, pojo3);

        write(list);

        assertEquals(startTable("string") + tr("one") + tr("two") + tr("three") + endTable(), result());
    }

    @Test
    public void shouldEncodeOneStringInputNamedPojoListAsTable() {
        OneStringInputNamedPojo pojo1 = new OneStringInputNamedPojo("one");
        OneStringInputNamedPojo pojo2 = new OneStringInputNamedPojo("two");
        OneStringInputNamedPojo pojo3 = new OneStringInputNamedPojo("three");
        List<OneStringInputNamedPojo> list = asList(pojo1, pojo2, pojo3);

        write(list);

        assertEquals(startTable("foo") + tr("one") + tr("two") + tr("three") + endTable(), result());
    }

    @Test
    public void shouldEncodeTwoFieldPojoListAsTable() {
        TwoFieldPojo pojo1 = new TwoFieldPojo("one", 111);
        TwoFieldPojo pojo2 = new TwoFieldPojo("two", 222);
        List<TwoFieldPojo> list = Arrays.asList(pojo1, pojo2);

        write(list);

        assertEquals(startTable("str", "i") //
                + tr("one", "111") //
                + tr("two", "222") //
                + endTable(), result());
    }

    @Test
    public void shouldEncodeTableWithListPojo() {
        writer.listWriter = mock(HtmlListWriter.class);
        doAnswer(writeDummyAnswer("list")).when(writer.listWriter).write(any(Item.class));
        ListPojo pojo1 = new ListPojo("dummy1", asList("one1", "two1", "three1"));
        ListPojo pojo2 = new ListPojo("dummy2", asList("one2", "two2", "three2"));
        List<ListPojo> list = asList(pojo1, pojo2);

        write(list);

        assertEquals(startTable("str", "list") + "<tr><td>{field:dummy1}</td><td>{list}</td></tr>"
                + "<tr><td>{field:dummy2}</td><td>{list}</td></tr>" + endTable(), result());
        verify(writer.listWriter, times(2)).write(captor.capture());
        List<Item> allValues = captor.getAllValues();
        assertEqualsListItem(allValues.get(0), "one1", "two1", "three1");
        assertEqualsListItem(allValues.get(1), "one2", "two2", "three2");
    }

    @Test
    public void shouldEncodeTableWithContainerPojo() {
        writer.linkWriter = mock(HtmlLinkWriter.class);
        doAnswer(writeDummyAnswer("link")).when(writer.linkWriter).write(any(Item.class), anyString());
        ContainerPojo pojo1 = new ContainerPojo("dummy1", new NestedPojo("foo", 123));
        ContainerPojo pojo2 = new ContainerPojo("dummy2", new NestedPojo("bar", 321));
        List<ContainerPojo> list = asList(pojo1, pojo2);

        write(list);

        assertEquals(startTable("str", "nested") //
                + "<tr><td>{field:dummy1}</td><td>{link}</td></tr>" //
                + "<tr><td>{field:dummy2}</td><td>{link}</td></tr>" //
                + endTable(), result());
        verify(writer.linkWriter, times(2)).write(captor.capture(), anyString());
        List<Item> allValues = captor.getAllValues();
        assertEquals("AbstractHtmlWriterTest.NestedPojo(str=foo, i=123)", allValues.get(0).toString());
        assertEquals("AbstractHtmlWriterTest.NestedPojo(str=bar, i=321)", allValues.get(1).toString());
    }
}
