package com.github.t1.webresource;

import static javax.xml.bind.annotation.XmlAccessType.*;
import static org.junit.Assert.*;

import java.io.Serializable;
import java.util.*;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;

import lombok.*;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class AdvancedItemTest {
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
        @Size(min = 1, max = 100)
        private String first;

        @XmlElement(name = "lastName")
        @Size(min = 1, max = 50)
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
    public void shouldGetProperties() throws Exception {
        Item<AdvancedItem> item = new Item<>(new AdvancedItem());

        assertEquals(100, item.trait("first").get(Size.class).max());
        assertEquals(50, item.trait("lastName").get(Size.class).max());
        assertTrue(item.trait("tags").is(XmlList.class));
        assertEquals(3, item.traits().size());
    }
}
