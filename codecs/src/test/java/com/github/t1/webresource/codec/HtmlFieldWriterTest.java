package com.github.t1.webresource.codec;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.*;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.*;

import org.junit.Test;

import com.github.t1.webresource.meta.*;

public class HtmlFieldWriterTest extends AbstractHtmlWriterTest {
    HtmlFieldWriter writer = new HtmlFieldWriter();

    private void write(Item item, Trait trait) {
        write(item, trait, null);
    }

    private void write(Item item, Trait trait, String id) {
        writer.out = out;
        writer.write(item, trait, id);
    }

    @Test
    public void shouldWriteSimpe() {
        Item item = Items.newItem("<dummy>");

        write(item, SimpleTrait.of(item));

        assertEquals("&lt;dummy&gt;", result());
    }

    @Test
    public void shouldWriteStringField() {
        Item item = Items.newItem(new OneStringPojo("foo"));

        write(item, item.trait("string"));

        assertEquals("<input name='string' class='string' type='text' value='foo'/>", result());
    }

    @Test
    public void shouldWriteStringFieldWithId() {
        Item item = Items.newItem(new OneStringPojo("foo"));

        write(item, item.trait("string"), "id-0");

        assertEquals("<input id='id-0' name='string' class='string' type='text' value='foo'/>", result());
    }

    @Data
    @AllArgsConstructor
    @XmlRootElement
    private static class BooleanPojo {
        private boolean b;
    }

    @Test
    public void shouldWriteBooleanField() {
        Item item = Items.newItem(new BooleanPojo(true));

        write(item, item.trait("b"));

        assertEquals("<input name='b' class='boolean' type='checkbox' value='true'/>", result());
    }

    @Data
    @AllArgsConstructor
    @XmlRootElement
    private static class DoublePojo {
        private double d;
    }

    @Test
    public void shouldWriteDoubleField() {
        Item item = Items.newItem(new DoublePojo(123.45));

        write(item, item.trait("d"));

        assertEquals("<input name='d' class='number' type='text' value='123.45'/>", result());
    }

    @Data
    @AllArgsConstructor
    @XmlRootElement
    private static class NumberPojo {
        private long b;
    }

    @Test
    public void shouldWriteIntegerField() {
        Item item = Items.newItem(new NumberPojo(123));

        write(item, item.trait("b"));

        assertEquals("<input name='b' class='number' type='text' value='123'/>", result());
    }

    @Data
    @AllArgsConstructor
    protected static class InputTypedPojo {
        @HtmlInputType("dummy")
        private String str;
    }

    @Test
    public void shouldWriteInputTypedPojo() {
        Item item = Items.newItem(new InputTypedPojo("foo"));

        write(item, item.trait("str"));

        assertEquals("<input name='str' class='string' type='dummy' value='foo'/>", result());
    }

    @Test
    public void shouldWriteNullTrait() {
        Item item = Items.newItem(Collections.singletonMap("foo", "bar"));

        write(item, new SimpleTrait(null));

        assertEquals(
                "<input name='value' class='null' type='text' value='MapItem[java.util.Collections$SingletonMap]'/>",
                result());
    }

    @Test
    public void shouldWriteUnknownTrait() {
        Item item = Items.newItem(new OneStringPojo("foo"));
        AbstractTrait trait = mock(AbstractTrait.class);
        when(trait.name()).thenReturn("bar");
        when(trait.type()).thenReturn("other");

        write(item, trait);

        assertEquals("<input name='bar' class='other' type='text' value=''/>", result());
    }

    @Test
    public void shouldWriteList() {
        writer.listWriter = mock(HtmlListWriter.class);
        doAnswer(writeDummyAnswer("list")).when(writer.listWriter).write(any(Item.class));
        Item item = Items.newItem(new ListPojo("foo", Arrays.asList("one", "two", "three")));

        write(item, item.trait("list"));

        assertEquals("{list}", result());
        verify(writer.listWriter).write(captor.capture());
        assertEqualsListItem(captor.getValue(), "one", "two", "three");
    }
}
