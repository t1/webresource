package com.github.t1.webresource.meta;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Test;

public class PojoGetterTraitTest {
    public static class OneGetter {
        public String getName() {
            return null;
        }
    }

    @Test
    public void shouldFindStringGetter() throws Exception {
        Method method = OneGetter.class.getMethod("getName");

        Trait trait = new PojoGetterTrait(method);

        assertEquals("name", trait.name());
    }
}
