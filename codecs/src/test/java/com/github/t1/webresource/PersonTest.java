package com.github.t1.webresource;

import static javax.xml.bind.annotation.XmlAccessType.*;
import static org.junit.Assert.*;

import java.io.Serializable;
import java.util.*;

import javax.validation.constraints.*;
import javax.xml.bind.annotation.*;

import lombok.*;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class PersonTest {
    @Getter
    @Setter
    @ToString
    @XmlRootElement
    @XmlAccessorType(NONE)
    public static class Person implements Serializable {
        private static final long serialVersionUID = 1L;

        @XmlTransient
        private Long id;

        @XmlElement
        @NotNull
        @Size(min = 1, max = 100)
        @Pattern(regexp = "\\p{Alpha}*", message = "must contain only alphabetical characters")
        private String first;

        @XmlElement(name = "lastName")
        @NotNull
        @Size(min = 1, max = 50)
        @Pattern(regexp = "\\p{Alpha}*", message = "must contain only alphabetical characters")
        private String last;

        @XmlTransient
        private Set<String> tags = new HashSet<>();

        /** required by JAXB */
        Person() {}

        public Person(String first, String last) {
            this.first = first;
            this.last = last;
        }

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
        Item<Person> item = new Item<>(new Person());

        assertEquals(100, item.trait("first").get(Size.class).max());
        assertEquals(50, item.trait("lastName").get(Size.class).max());
        assertTrue(item.trait("tags").is(XmlList.class));
        assertEquals(3, item.traits().size());
    }
}
