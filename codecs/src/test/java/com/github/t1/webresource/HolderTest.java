package com.github.t1.webresource;

import static org.junit.Assert.*;

import java.util.*;

import javax.xml.bind.annotation.*;

import lombok.*;

import org.junit.Test;

public class HolderTest {

    @Test
    public void shouldHoldNull() throws Exception {
        Holder<?> holder = new Holder<>(null);

        assertTrue(holder.isNull());
        assertTrue(holder.isSimple());
        assertNull(holder.get(Holder.SIMPLE));
        assertEquals(Arrays.asList(Holder.SIMPLE), holder.properties());

        assertFalse(holder.is(XmlRootElement.class));
        assertNull(holder.get(XmlRootElement.class));
    }

    @Test
    public void shouldHoldString() throws Exception {
        Holder<String> holder = new Holder<>("dummy");

        assertFalse(holder.isNull());
        assertTrue(holder.isSimple());
        assertEquals("dummy", holder.get(Holder.SIMPLE));
        assertEquals(Arrays.asList(Holder.SIMPLE), holder.properties());

        assertFalse(holder.is(XmlRootElement.class));
        assertNull(holder.get(XmlRootElement.class));
    }

    @Test
    public void shouldHoldLong() throws Exception {
        Holder<Long> holder = new Holder<>(1234L);

        assertFalse(holder.isNull());
        assertTrue(holder.isSimple());
        assertEquals(1234L, holder.get(Holder.SIMPLE));
        assertEquals(Arrays.asList(Holder.SIMPLE), holder.properties());

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

        assertOneTwoThree(holder);

        assertFalse(holder.is(XmlRootElement.class));
        assertNull(holder.get(XmlRootElement.class));

        Property one = holder.properties().get(0);
        assertFalse(one.is(XmlAttribute.class));
        assertNull(one.get(XmlAttribute.class));

        Property two = holder.properties().get(1);
        assertFalse(two.is(XmlAttribute.class));
        assertNull(two.get(XmlAttribute.class));
    }

    private void assertOneTwoThree(Holder<?> holder) {
        List<Property> properties = holder.properties();
        assertEquals(3, properties.size());
        Property one = properties.get(0);
        Property two = properties.get(1);
        Property three = properties.get(2);

        assertEquals("one", one.getName());
        assertEquals("two", two.getName());
        assertEquals("three", three.getName());

        assertEquals("111", holder.get(one));
        assertEquals("222", holder.get(two));
        assertEquals("333", holder.get(three));
    }

    @AllArgsConstructor
    public static class Pojo {
        @XmlAttribute
        public String one;
        public String two;
        public String three;
    }

    @Test
    public void shouldHoldPojo() throws Exception {
        Holder<Pojo> holder = new Holder<>(new Pojo("111", "222", "333"));

        assertFalse(holder.isNull());
        assertFalse(holder.isSimple());

        assertOneTwoThree(holder);

        assertFalse(holder.is(XmlRootElement.class));
        assertNull(holder.get(XmlRootElement.class));

        Property one = holder.properties().get(0);
        assertTrue(one.is(XmlAttribute.class));
        assertNotNull(one.get(XmlAttribute.class));

        Property two = holder.properties().get(1);
        assertFalse(two.is(XmlAttribute.class));
        assertNull(two.get(XmlAttribute.class));
    }

    @Data
    @AllArgsConstructor
    @XmlRootElement
    public static class JaxbPojo {
        @XmlAttribute
        private String one;
        private String two;
        private String three;
    }

    @Test
    public void shouldHoldJaxbPojo() throws Exception {
        Holder<JaxbPojo> holder = new Holder<>(new JaxbPojo("111", "222", "333"));

        assertFalse(holder.isNull());
        assertFalse(holder.isSimple());

        assertOneTwoThree(holder);

        assertTrue(holder.is(XmlRootElement.class));
        assertNotNull(holder.get(XmlRootElement.class));

        Property one = holder.properties().get(0);
        assertTrue(one.is(XmlAttribute.class));
        assertNotNull(one.get(XmlAttribute.class));

        Property two = holder.properties().get(1);
        assertFalse(two.is(XmlAttribute.class));
        assertNull(two.get(XmlAttribute.class));
    }
}
