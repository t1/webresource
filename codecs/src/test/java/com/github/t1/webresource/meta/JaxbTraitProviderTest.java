package com.github.t1.webresource.meta;

import static org.junit.Assert.*;

import java.util.List;

import javax.xml.bind.annotation.*;

import lombok.*;

import org.junit.Test;

public class JaxbTraitProviderTest {
    @Data
    @XmlRootElement
    public static class Pojo {
        private String one;
    }

    @Test
    public void shouldConsiderPublicGetter() {
        List<Trait> traits = new JaxbPojoTraitCollector(Pojo.class).run();

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
    public void shouldConsiderAccessorOrder() {
        List<Trait> traits = new JaxbPojoTraitCollector(AlphabeticalPojo.class).run();

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
    public void shouldConsiderPropOrder1() {
        List<Trait> traits = new JaxbPojoTraitCollector(Pojo1.class).run();

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
    public void shouldConsiderPropOrder2() {
        List<Trait> traits = new JaxbPojoTraitCollector(Pojo2.class).run();

        assertEquals(2, traits.size());
        assertEquals("two", traits.get(0).name());
        assertEquals("one", traits.get(1).name());
    }


    @XmlRootElement
    public static class StaticFieldAndGetter {
        @Getter
        public static String one;
    }

    @Test
    public void shouldNotFindStaticGetter() {
        List<Trait> traits = new JaxbPojoTraitCollector(StaticFieldAndGetter.class).run();

        assertEquals(0, traits.size());
    }
}
