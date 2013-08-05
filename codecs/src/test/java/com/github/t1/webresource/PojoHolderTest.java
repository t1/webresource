package com.github.t1.webresource;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class PojoHolderTest {

    @Test
    public void shouldHoldNull() throws Exception {
        PojoHolder pojo = new PojoHolder(null);

        assertTrue(pojo.isNull());
        assertTrue(pojo.isSimple());
        assertNull(pojo.get(PojoProperty.SIMPLE));
        assertEquals(Arrays.asList(PojoProperty.SIMPLE), pojo.properties());
    }

    @Test
    public void shouldHoldString() throws Exception {
        PojoHolder pojo = new PojoHolder("dummy");

        assertFalse(pojo.isNull());
        assertTrue(pojo.isSimple());
        assertEquals("dummy", pojo.get(PojoProperty.SIMPLE));
        assertEquals(Arrays.asList(PojoProperty.SIMPLE), pojo.properties());
    }

    @Test
    public void shouldHoldLong() throws Exception {
        PojoHolder pojo = new PojoHolder(1234L);

        assertFalse(pojo.isNull());
        assertTrue(pojo.isSimple());
        assertEquals("1234", pojo.get(PojoProperty.SIMPLE));
        assertEquals(Arrays.asList(PojoProperty.SIMPLE), pojo.properties());
    }
}
