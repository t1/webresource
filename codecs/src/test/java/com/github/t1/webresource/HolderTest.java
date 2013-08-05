package com.github.t1.webresource;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class HolderTest {

    @Test
    public void shouldHoldNull() throws Exception {
        Holder pojo = new Holder(null);

        assertTrue(pojo.isNull());
        assertTrue(pojo.isSimple());
        assertNull(pojo.get(Property.SIMPLE));
        assertEquals(Arrays.asList(Property.SIMPLE), pojo.properties());
    }

    @Test
    public void shouldHoldString() throws Exception {
        Holder pojo = new Holder("dummy");

        assertFalse(pojo.isNull());
        assertTrue(pojo.isSimple());
        assertEquals("dummy", pojo.get(Property.SIMPLE));
        assertEquals(Arrays.asList(Property.SIMPLE), pojo.properties());
    }

    @Test
    public void shouldHoldLong() throws Exception {
        Holder pojo = new Holder(1234L);

        assertFalse(pojo.isNull());
        assertTrue(pojo.isSimple());
        assertEquals("1234", pojo.get(Property.SIMPLE));
        assertEquals(Arrays.asList(Property.SIMPLE), pojo.properties());
    }
}
