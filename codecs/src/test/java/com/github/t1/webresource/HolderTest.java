package com.github.t1.webresource;

import static org.junit.Assert.*;

import java.util.*;

import javax.xml.bind.annotation.XmlRootElement;

import org.junit.Test;

public class HolderTest {

    @Test
    public void shouldHoldNull() throws Exception {
        Holder<?> holder = new Holder<>(null);

        assertTrue(holder.isNull());
        assertTrue(holder.isSimple());
        assertNull(holder.get(FieldProperty.SIMPLE));
        assertEquals(Arrays.asList(FieldProperty.SIMPLE), holder.properties());

        assertFalse(holder.is(XmlRootElement.class));
        assertNull(holder.get(XmlRootElement.class));
    }

    @Test
    public void shouldHoldString() throws Exception {
        Holder<String> holder = new Holder<>("dummy");

        assertFalse(holder.isNull());
        assertTrue(holder.isSimple());
        assertEquals("dummy", holder.get(FieldProperty.SIMPLE));
        assertEquals(Arrays.asList(FieldProperty.SIMPLE), holder.properties());

        assertFalse(holder.is(XmlRootElement.class));
        assertNull(holder.get(XmlRootElement.class));
    }

    @Test
    public void shouldHoldLong() throws Exception {
        Holder<Long> holder = new Holder<>(1234L);

        assertFalse(holder.isNull());
        assertTrue(holder.isSimple());
        assertEquals("1234", holder.get(FieldProperty.SIMPLE));
        assertEquals(Arrays.asList(FieldProperty.SIMPLE), holder.properties());

        assertFalse(holder.is(XmlRootElement.class));
        assertNull(holder.get(XmlRootElement.class));
    }

    @Test
    public void shouldHoldMap() throws Exception {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("one", "111");
        map.put("two", "222");
        map.put("three", "333");
        Holder<Map<String, String>> holder = new Holder<>(map);

        assertFalse(holder.isNull());
        assertFalse(holder.isSimple());
        List<Property> properties = holder.properties();
        assertEquals(3, properties.size());
        assertEquals("one", properties.get(0).getName());
        assertEquals("two", properties.get(1).getName());
        assertEquals("three", properties.get(2).getName());

        assertFalse(holder.is(XmlRootElement.class));
        assertNull(holder.get(XmlRootElement.class));
    }
}
