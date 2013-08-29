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
    }

    @Test
    public void shouldHoldString() throws Exception {
        Holder<String> holder = new Holder<>("dummy");

        assertFalse(holder.isNull());
        assertTrue(holder.isSimple());
        assertEquals("dummy", holder.get(Holder.SIMPLE));
        assertEquals(Arrays.asList(Holder.SIMPLE), holder.properties());
    }

    @Test
    public void shouldHoldLong() throws Exception {
        Holder<Long> holder = new Holder<>(1234L);

        assertFalse(holder.isNull());
        assertTrue(holder.isSimple());
        assertEquals(1234L, holder.get(Holder.SIMPLE));
        assertEquals(Arrays.asList(Holder.SIMPLE), holder.properties());
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

        Property one = properties.get(0);
        assertEquals("one", one.getName());
        assertEquals("111", holder.get(one));

        Property two = properties.get(1);
        assertEquals("two", two.getName());
        assertEquals("222", holder.get(two));

        Property three = properties.get(2);
        assertEquals("three", three.getName());
        assertEquals("333", holder.get(three));
    }

    @AllArgsConstructor
    public static class Pojo {
        public String one;
        public String two;
        public String three;
    }

    @Test
    public void shouldHoldPojo() throws Exception {
        Holder<Pojo> holder = new Holder<>(new Pojo("111", "222", "333"));

        assertFalse(holder.isNull());
        assertFalse(holder.isSimple());

        List<Property> properties = holder.properties();
        assertEquals(3, properties.size());
        Property one = properties.get(0);
        Property two = properties.get(1);
        Property three = properties.get(2);

        assertEquals("one", one.getName());
        assertEquals("111", holder.get(one));

        assertEquals("two", two.getName());
        assertEquals("222", holder.get(two));

        assertEquals("three", three.getName());
        assertEquals("333", holder.get(three));
    }

    @Data
    @XmlRootElement
    @AllArgsConstructor
    public static class JaxbPojo {
        private String one;
        private String two;
        private String three;

        @XmlAttribute(name = "xxx")
        public String getOne() {
            return one;
        }

        @XmlElement(name = "yyy")
        public String getTwo() {
            return two;
        }

        @XmlElement(name = "zzz")
        public String getThree() {
            return three;
        }
    }

    @Test
    public void shouldHoldJaxbPojo() throws Exception {
        Holder<JaxbPojo> holder = new Holder<>(new JaxbPojo("111", "222", "333"));

        assertFalse(holder.isNull());
        assertFalse(holder.isSimple());

        List<Property> properties = holder.properties();
        assertEquals(3, properties.size());

        Property one = properties.get(0);
        assertEquals("xxx", one.getName());
        assertEquals("111", holder.get(one));

        Property two = properties.get(1);
        assertEquals("two", two.getName());
        assertEquals("222", holder.get(two));

        Property three = properties.get(2);
        assertEquals("three", three.getName());
        assertEquals("333", holder.get(three));
    }
}
