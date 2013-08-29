package com.github.t1.webresource;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Test;

public class GetterPropertyTest {
    public static class OneGetter {
        public String getName() {
            return null;
        }
    }

    @Test
    public void shouldFindStringGetter() throws Exception {
        Method method = OneGetter.class.getMethod("getName");

        Property property = new PojoGetterProperty(method);

        assertEquals("name", property.getName());
    }
}
