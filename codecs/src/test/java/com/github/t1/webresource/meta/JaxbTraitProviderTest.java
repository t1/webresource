package com.github.t1.webresource.meta;

import static org.junit.Assert.*;

import javax.xml.bind.annotation.*;

import lombok.Data;

import org.junit.Test;

public class JaxbTraitProviderTest {
    @Data
    @XmlRootElement
    public static class Pojo {
        private String one;
    }

    @Test
    public void shouldConsiderPublicGetter() throws Exception {
        PojoTraits traits = new PojoTraits(Pojo.class);

        assertEquals(1, traits.size());
        assertEquals("one", traits.get(0).name());
    }

    @Data
    @XmlRootElement
    @XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
    public static class AlphabeticalPojo {
        private String two, one;
    }

    @Test
    public void shouldConsiderAccessorOrder() throws Exception {
        PojoTraits traits = new PojoTraits(AlphabeticalPojo.class);

        assertEquals(2, traits.size());
        assertEquals("one", traits.get(0).name());
        assertEquals("two", traits.get(1).name());
    }

    @Data
    @XmlRootElement
    @XmlType(propOrder = { "one", "two" })
    public static class Pojo1 {
        private String one, two;
    }

    @Test
    public void shouldConsiderPropOrder1() throws Exception {
        PojoTraits traits = new PojoTraits(Pojo1.class);

        assertEquals(2, traits.size());
        assertEquals("one", traits.get(0).name());
        assertEquals("two", traits.get(1).name());
    }

    @Data
    @XmlRootElement
    @XmlType(propOrder = { "two", "one" })
    public static class Pojo2 {
        private String one, two;
    }

    @Test
    public void shouldConsiderPropOrder2() throws Exception {
        PojoTraits traits = new PojoTraits(Pojo2.class);

        assertEquals(2, traits.size());
        assertEquals("two", traits.get(0).name());
        assertEquals("one", traits.get(1).name());
    }
}
