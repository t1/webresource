package com.github.t1.webresource;

import static org.junit.Assert.*;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlRootElement;

import org.junit.Test;

public class HolderTest {

    @Test
    public void shouldHoldNull() throws Exception {
        Holder holder = new Holder(null);

        assertTrue(holder.isNull());
        assertTrue(holder.isSimple());
        assertNull(holder.get(Property.SIMPLE));
        assertEquals(Arrays.asList(Property.SIMPLE), holder.properties());

        assertFalse(holder.is(XmlRootElement.class));
        assertNull(holder.get(XmlRootElement.class));
    }

    @Test
    public void shouldHoldString() throws Exception {
        Holder holder = new Holder("dummy");

        assertFalse(holder.isNull());
        assertTrue(holder.isSimple());
        assertEquals("dummy", holder.get(Property.SIMPLE));
        assertEquals(Arrays.asList(Property.SIMPLE), holder.properties());

        assertFalse(holder.is(XmlRootElement.class));
        assertNull(holder.get(XmlRootElement.class));
    }

    @Test
    public void shouldHoldLong() throws Exception {
        Holder holder = new Holder(1234L);

        assertFalse(holder.isNull());
        assertTrue(holder.isSimple());
        assertEquals("1234", holder.get(Property.SIMPLE));
        assertEquals(Arrays.asList(Property.SIMPLE), holder.properties());

        assertFalse(holder.is(XmlRootElement.class));
        assertNull(holder.get(XmlRootElement.class));
    }
}
