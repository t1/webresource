package com.github.t1.webresource.codec;

import static java.util.Arrays.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.Test;
import org.mockito.InjectMocks;

import com.github.t1.webresource.meta.*;

public class HtmlBodyWriterTest extends AbstractHtmlWriterTest {
    @InjectMocks
    HtmlBodyWriter writer;

    private void write(Object t) {
        writer.out = out;
        writer.write(Items.newItem(t));
    }

    @Test
    public void shouldEncodeNullObject() {
        write(null);

        assertEquals("", result());
    }

    @Test
    public void shouldEncodePrimitiveString() {
        write("dummy");

        assertEquals("dummy", result());
    }

    @Test
    public void shouldEscapeString() {
        write("string & ampersand");

        assertEquals("string &amp; ampersand", result());
    }

    @Test
    public void shouldEncodePrimitiveInteger() {
        write(1234);

        assertEquals("1234", result());
    }

    @Test
    public void shouldEncodeEmptyList() {
        List<String> list = Collections.emptyList();

        write(list);

        assertEquals("", result());
    }

    @Test
    public void shouldEncodeNoTraitItem() {
        List<NoTraitPojo> list = asList(new NoTraitPojo(), new NoTraitPojo());

        write(list);

        assertEquals("", result());
    }

    @Test
    public void shouldEncodeStringList() {
        writer.listWriter = mock(HtmlListWriter.class);
        List<String> list = asList("one", "two", "three");

        write(list);

        verify(writer.listWriter).write(captor.capture());
        assertEqualsListItem(captor.getValue(), "one", "two", "three");
    }

    @Test
    public void shouldEncodeTable() {
        writer.tableWriter = mock(HtmlTableWriter.class);
        List<OneStringPojo> list =
                asList(new OneStringPojo("one"), new OneStringPojo("two"), new OneStringPojo("three"));

        write(list);

        verify(writer.tableWriter).write(captor.capture());
        assertEqualsListItem(captor.getValue(), "{one}", "{two}", "{three}");
    }

    @Test
    public void shouldEncodeForm() {
        writer.formWriter = mock(HtmlFormWriter.class);
        NoTraitPojo pojo = new NoTraitPojo();

        write(pojo);

        verify(writer.formWriter).write(captor.capture());
        Item item = captor.getValue();
        assertEquals("the-pojo", item.toString());
    }

    @Test
    public void shouldEncodeType() {
        writer.linkWriter = mock(HtmlLinkWriter.class);
        doAnswer(writeAnswer("link")).when(writer.linkWriter).write(any(Item.class), anyString());

        write(NoTraitPojo.class);

        assertEquals("{link:notraitpojos}", result());
        verify(writer.linkWriter).write(captor.capture(), anyString());
        assertEquals("notraitpojos", captor.getValue().toString());
    }

    @Test
    public void shouldEncodeListOfTypes() {
        writer.listWriter = mock(HtmlListWriter.class);
        doAnswer(writeDummyAnswer("list")).when(writer.listWriter).write(any(Item.class));
        List<Class<? extends Object>> list = Arrays.asList(NoTraitPojo.class, OneStringPojo.class);

        write(list);

        assertEquals("{list}", result());
        verify(writer.listWriter).write(captor.capture());
        assertEquals("ListItem[java.util.Arrays$ArrayList]", captor.getValue().toString());
    }
}
