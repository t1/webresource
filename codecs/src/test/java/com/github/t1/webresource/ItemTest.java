package com.github.t1.webresource;

import static org.junit.Assert.*;

import java.util.*;

import javax.xml.bind.annotation.*;

import lombok.*;

import org.junit.Test;

public class ItemTest {

    @Test
    public void shouldHoldNull() throws Exception {
        Item<?> item = new Item<>(null);

        assertTrue(item.isNull());
        assertTrue(item.isSimple());
        assertNull(item.get(Item.SIMPLE));
        assertEquals(Arrays.asList(Item.SIMPLE), item.traits());
    }

    @Test
    public void shouldHoldString() throws Exception {
        Item<String> item = new Item<>("dummy");

        assertFalse(item.isNull());
        assertTrue(item.isSimple());
        assertEquals("dummy", item.get(Item.SIMPLE));
        assertEquals(Arrays.asList(Item.SIMPLE), item.traits());
    }

    @Test
    public void shouldHoldLong() throws Exception {
        Item<Long> item = new Item<>(1234L);

        assertFalse(item.isNull());
        assertTrue(item.isSimple());
        assertEquals(1234L, item.get(Item.SIMPLE));
        assertEquals(Arrays.asList(Item.SIMPLE), item.traits());
    }

    @Test
    public void shouldHoldMap() throws Exception {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("one", "111");
        map.put("two", "222");
        map.put("three", "333");
        Item<Map<String, String>> item = new Item<>(map);

        assertFalse(item.isNull());
        assertFalse(item.isSimple());

        assertEquals(3, item.traits().size());

        Trait one = item.trait("one");
        assertEquals("111", item.get(one));

        Trait two = item.trait("two");
        assertEquals("222", item.get(two));

        Trait three = item.trait("three");
        assertEquals("333", item.get(three));
    }

    @AllArgsConstructor
    public static class Pojo {
        public String one;
        public String two;
        public String three;
    }

    @Test
    public void shouldHoldPojo() throws Exception {
        Item<Pojo> item = new Item<>(new Pojo("111", "222", "333"));

        assertFalse(item.isNull());
        assertFalse(item.isSimple());

        assertEquals(3, item.traits().size());

        Trait one = item.trait("one");
        assertEquals("111", item.get(one));

        Trait two = item.trait("two");
        assertEquals("222", item.get(two));

        Trait three = item.trait("three");
        assertEquals("333", item.get(three));
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
        Item<JaxbPojo> item = new Item<>(new JaxbPojo("111", "222", "333"));

        assertFalse(item.isNull());
        assertFalse(item.isSimple());

        assertEquals(3, item.traits().size());

        Trait one = item.trait("xxx");
        assertEquals("111", item.get(one));

        Trait two = item.trait("yyy");
        assertEquals("222", item.get(two));

        Trait three = item.trait("zzz");
        assertEquals("333", item.get(three));
    }
}
