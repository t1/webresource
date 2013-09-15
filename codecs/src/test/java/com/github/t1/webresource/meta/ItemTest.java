package com.github.t1.webresource.meta;

import static com.github.t1.webresource.meta.SimpleTrait.*;
import static javax.xml.bind.annotation.XmlAccessType.*;
import static org.junit.Assert.*;

import java.io.Serializable;
import java.lang.annotation.*;
import java.util.*;

import javax.xml.bind.annotation.*;

import lombok.*;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class ItemTest {

    @Test
    public void shouldHoldNull() throws Exception {
        Item item = Items.newItem(null);

        assertTrue(item.isNull());
        assertTrue(item.isSimple());
        assertNull(item.get(SIMPLE));
        assertEquals(Arrays.asList(SIMPLE), item.traits());
    }

    @Test
    public void shouldHoldString() throws Exception {
        Item item = Items.newItem("dummy");

        assertFalse(item.isNull());
        assertTrue(item.isSimple());
        assertEquals("dummy", item.get(SIMPLE));
        assertEquals(Arrays.asList(SIMPLE), item.traits());
    }

    @Test
    public void shouldHoldLong() throws Exception {
        Item item = Items.newItem(1234L);

        assertFalse(item.isNull());
        assertTrue(item.isSimple());
        assertEquals(1234L, item.get(SIMPLE));
        assertEquals(Arrays.asList(SIMPLE), item.traits());
    }

    @Test
    public void shouldHoldMap() throws Exception {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("one", "111");
        map.put("two", "222");
        map.put("three", "333");
        Item item = Items.newItem(map);

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
        Item item = Items.newItem(new Pojo("111", "222", "333"));

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
        Item item = Items.newItem(new JaxbPojo("111", "222", "333"));

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
        private Long id;

        @XmlElement
        @Size(100)
        private String first;

        @XmlElement(name = "lastName")
        @Size(50)
        private String last;

        @XmlTransient
        private Set<String> tags = new HashSet<>();

        @XmlList
        @XmlElement(name = "tags")
        public List<String> getTagList() {
            ImmutableList.Builder<String> builder = ImmutableList.builder();
            for (String tag : tags) {
                builder.add(tag);
            }
            return builder.build();
        }
    }

    @Test
    public void shouldHoldProperties() throws Exception {
        Item item = Items.newItem(new AdvancedItem());

        assertEquals(100, item.trait("first").get(Size.class).value());
        assertEquals(50, item.trait("lastName").get(Size.class).value());
        assertTrue(item.trait("tags").is(XmlList.class));
        assertEquals(3, item.traits().size());
    }
}
