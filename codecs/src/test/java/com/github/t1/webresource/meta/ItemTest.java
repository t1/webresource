package com.github.t1.webresource.meta;

import static java.util.Arrays.*;
import static javax.xml.bind.annotation.XmlAccessType.*;
import static org.junit.Assert.*;

import java.io.Serializable;
import java.lang.annotation.*;
import java.util.*;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.*;

import lombok.*;

import org.junit.Test;

public class ItemTest {

    @Test
    public void shouldHoldNull() {
        Item item = Items.newItem(null);

        assertTrue(item.isNull());
        assertTrue(item.isSimple());
        assertEquals("", item.read(new SimpleTrait(null)).toString());
        assertEquals("[SimpleTrait[null]]", item.traits().toString());
    }

    @Test
    public void shouldHoldString() {
        Item item = Items.newItem("dummy");

        assertFalse(item.isNull());
        assertTrue(item.isSimple());
        assertEquals("dummy", item.read(new SimpleTrait(String.class)).toString());
        assertEquals("[SimpleTrait[string]]", item.traits().toString());
    }

    @Test
    public void shouldHoldLong() {
        Item item = Items.newItem(1234L);

        assertFalse(item.isNull());
        assertTrue(item.isSimple());
        assertEquals("1234", item.read(new SimpleTrait(Long.class)).toString());
        assertEquals("[SimpleTrait[number]]", item.traits().toString());
    }

    @Test
    public void shouldHoldDate() {
        Item item = Items.newItem(new Date(0));

        assertFalse(item.isNull());
        assertTrue(item.isSimple());
        assertEquals("Thu Jan 01 01:00:00 CET 1970", item.read(new SimpleTrait(Date.class)).toString());
        assertEquals("[SimpleTrait[date]]", item.traits().toString());
    }

    @Test
    public void shouldHoldMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("one", "111");
        map.put("two", "222");
        map.put("three", "333");
        Item item = Items.newItem(map);

        assertFalse(item.isNull());
        assertFalse(item.isSimple());

        assertEquals(3, item.traits().size());

        Trait one = item.trait("one");
        assertEquals("111", item.read(one).toString());

        Trait two = item.trait("two");
        assertEquals("222", item.read(two).toString());

        Trait three = item.trait("three");
        assertEquals("333", item.read(three).toString());
    }

    @AllArgsConstructor
    public static class Pojo {
        public String one;
        public String two;
        public String three;
    }

    @Test
    public void shouldHoldPojo() {
        Item item = Items.newItem(new Pojo("111", "222", "333"));

        assertFalse(item.isNull());
        assertFalse(item.isSimple());

        assertEquals(3, item.traits().size());

        Trait one = item.trait("one");
        assertEquals("111", item.read(one).toString());

        Trait two = item.trait("two");
        assertEquals("222", item.read(two).toString());

        Trait three = item.trait("three");
        assertEquals("333", item.read(three).toString());
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
    public void shouldHoldJaxbPojo() {
        Item item = Items.newItem(new JaxbPojo("111", "222", "333"));

        assertFalse(item.isNull());
        assertFalse(item.isSimple());

        assertEquals(3, item.traits().size());

        Trait one = item.trait("xxx");
        assertEquals("111", item.read(one).toString());

        Trait two = item.trait("yyy");
        assertEquals("222", item.read(two).toString());

        Trait three = item.trait("zzz");
        assertEquals("333", item.read(three).toString());
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Size {
        int value();
    }

    @Getter
    @Setter
    @ToString
    @XmlRootElement
    @XmlAccessorType(NONE)
    public static class AdvancedItem implements Serializable {
        private static final long serialVersionUID = 1L;

        @XmlTransient
        private Long id = 123L;

        @XmlElement
        @Size(100)
        private String first = "fff";

        @XmlElement(name = "lastName")
        @Size(50)
        private String last = "lll";

        private Set<String> tags = new LinkedHashSet<>(asList("one", "two"));

        @XmlList
        @XmlElement(name = "tags")
        public List<String> getTagList() {
            List<String> list = new ArrayList<>();
            for (String tag : tags) {
                list.add(tag);
            }
            return list;
        }

        // not an XmlElement or XmlAttribute, and AccessorType is NONE
        public String getNever() {
            return "nope";
        }
    }

    @Test
    public void shouldMarshalAdvancedItem() {
        JAXB.marshal(new AdvancedItem(), System.out);

        // no asserts... just check that the annotations are legal
    }

    @Test
    public void shouldHoldProperties() {
        Item item = Items.newItem(new AdvancedItem());

        assertEquals("first, lastName, tags", names(item.traits()));
        assertEquals("123", item.read(item.trait("id")).toString());
        assertEquals("fff", item.read(item.trait("first")).toString());
        assertEquals("lll", item.read(item.trait("last")).toString());
        assertEquals(100, item.trait("first").get(Size.class).value());
        assertEquals(50, item.trait("lastName").get(Size.class).value());
        assertTrue(item.trait("tags").is(XmlList.class)); // not the field; the method getTagList!
        assertEquals(3, item.traits().size());
    }

    private String names(Collection<Trait> traits) {
        StringBuilder out = new StringBuilder();
        for (Trait trait : traits) {
            if (out.length() > 0)
                out.append(", ");
            out.append(trait.name());
        }
        return out.toString();
    }
}
