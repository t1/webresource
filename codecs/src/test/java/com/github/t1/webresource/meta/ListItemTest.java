package com.github.t1.webresource.meta;

import static org.junit.Assert.*;

import java.lang.annotation.Annotation;
import java.util.List;

import org.junit.Test;

import com.github.t1.webresource.codec.*;
import com.google.common.collect.ImmutableList;

public class ListItemTest {

    @HtmlInputType("dummy")
    @HtmlStyleSheet("dummy")
    static class A {}

    static class B {}

    @Test
    public void shouldFindClassAnnotationsInList() throws Exception {
        List<Class<?>> list = ImmutableList.of(A.class, B.class);
        ListItem item = new ListItem(list);

        Annotation[] annotations = item.annotations().getAnnotations();

        assertEquals(2, annotations.length);
        // the order is not guaranteed
        if (annotations[0] instanceof HtmlInputType) {
            assertTrue(annotations[1] instanceof HtmlStyleSheet);
        } else {
            assertTrue(annotations[0] instanceof HtmlStyleSheet);
            assertTrue(annotations[1] instanceof HtmlInputType);
        }
    }
}
