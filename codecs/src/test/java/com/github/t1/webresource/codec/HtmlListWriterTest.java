package com.github.t1.webresource.codec;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.Test;

import com.github.t1.webresource.meta.*;

public class HtmlListWriterTest extends AbstractHtmlWriterTest {
    HtmlListWriter writer = new HtmlListWriter();

    private void write(Object pojo) {
        writer.out = out;
        writer.write(Items.newItem(pojo));
    }

    @Test
    public void shouldWriteEmptyList() {
        List<String> list = Collections.emptyList();

        write(list);

        assertEquals("<ul class='empty'></ul>", result());
    }

    @Test
    public void shouldWriteSimpleList() {
        List<String> list = Arrays.asList("one", "two", "three");

        write(list);

        assertEquals("<ul class='strings'><li>one</li><li>two</li><li>three</li></ul>", result());
    }

    @Test
    public void shouldWritePojoList() {
        writer.linkWriter = mock(HtmlLinkWriter.class);
        doAnswer(writeDummyAnswer("link")).when(writer.linkWriter).write(any(Item.class), anyString());
        List<OneStringPojo> list = Arrays.asList(new OneStringPojo("one"), new OneStringPojo("two"));

        write(list);

        assertEquals("<ul class='onestringpojos'><li>{link}</li><li>{link}</li></ul>", result());
        verify(writer.linkWriter, times(2)).write(captor.capture(), anyString());
        List<Item> values = captor.getAllValues();
        assertEquals(2, values.size());
        assertEquals("{one}", values.get(0).toString());
        assertEquals("{two}", values.get(1).toString());
    }
}
