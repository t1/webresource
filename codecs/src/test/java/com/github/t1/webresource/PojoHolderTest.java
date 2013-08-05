package com.github.t1.webresource;

import static org.junit.Assert.*;

import org.junit.Test;

public class PojoHolderTest {

    @Test
    public void shouldHoldNull() throws Exception {
        PojoHolder pojo = new PojoHolder(null);

        assertTrue(pojo.isNull());
        assertNull(pojo.get(PojoProperty.SIMPLE));
    }

    @Test
    public void shouldHoldString() throws Exception {
        PojoHolder pojo = new PojoHolder("dummy");

        assertFalse(pojo.isNull());
        assertEquals("dummy", pojo.get(PojoProperty.SIMPLE));
    }

    @Test
    public void shouldHoldLong() throws Exception {
        PojoHolder pojo = new PojoHolder(1234L);

        assertFalse(pojo.isNull());
        assertEquals("1234", pojo.get(PojoProperty.SIMPLE));
    }
}
