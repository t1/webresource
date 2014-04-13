package com.github.t1.webresource.meta;

import static org.junit.Assert.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.*;

import javax.xml.bind.annotation.*;

import org.junit.Test;

import com.github.t1.stereotypes.Annotations;

public class UnionAnnotatedElementTest {

    public static class ClassEmpty {}

    @XmlRootElement(name = "root-a")
    public static class ClassA {}

    @XmlType
    @XmlRootElement(name = "root-b")
    public static class ClassB {}

    private static final AnnotatedElement EMPTY = Annotations.on(ClassEmpty.class);
    private static final AnnotatedElement A = Annotations.on(ClassA.class);
    private static final AnnotatedElement B = Annotations.on(ClassB.class);

    private void assertAnnotationTypes(Annotation[] annotations, Class<?>... types) {
        assertEquals(annotations.length, types.length);
        List<Class<?>> annotationTypes = new ArrayList<>();
        for (Annotation annotation : annotations) {
            annotationTypes.add(annotation.annotationType());
        }
        for (Class<?> type : types) {
            assertTrue(annotationTypes.contains(type));
        }
    }

    @Test
    public void shouldStartEmpty() {
        UnionAnnotatedElement union = new UnionAnnotatedElement();

        assertEquals(0, union.getAnnotations().length);
        assertEquals(0, union.getDeclaredAnnotations().length);

        assertFalse(union.isAnnotationPresent(XmlRootElement.class));
        assertNull(union.getAnnotation(XmlRootElement.class));
    }

    @Test
    public void shouldCollectEmpty() {
        UnionAnnotatedElement union = new UnionAnnotatedElement(EMPTY);

        assertEquals(0, union.getAnnotations().length);
        assertEquals(0, union.getDeclaredAnnotations().length);

        assertFalse(union.isAnnotationPresent(XmlRootElement.class));
        assertNull(union.getAnnotation(XmlRootElement.class));
    }

    @Test
    public void shouldCollectA() {
        UnionAnnotatedElement union = new UnionAnnotatedElement(A);

        assertAnnotationTypes(union.getAnnotations(), XmlRootElement.class);
        assertAnnotationTypes(union.getDeclaredAnnotations(), XmlRootElement.class);

        assertEquals("root-a", union.getAnnotation(XmlRootElement.class).name());

        assertTrue(union.isAnnotationPresent(XmlRootElement.class));
        assertFalse(union.isAnnotationPresent(XmlType.class));
        assertNull(union.getAnnotation(XmlType.class));
    }

    @Test
    public void shouldCollectB() {
        UnionAnnotatedElement union = new UnionAnnotatedElement(B);

        assertAnnotationTypes(union.getAnnotations(), XmlType.class, XmlRootElement.class);
        assertAnnotationTypes(union.getDeclaredAnnotations(), XmlType.class, XmlRootElement.class);

        assertEquals("root-b", union.getAnnotation(XmlRootElement.class).name());

        assertTrue(union.isAnnotationPresent(XmlRootElement.class));
        assertTrue(union.isAnnotationPresent(XmlType.class));
    }

    @Test
    public void shouldMergeAandB() {
        UnionAnnotatedElement union = new UnionAnnotatedElement(A, B);

        assertAnnotationTypes(union.getAnnotations(), XmlRootElement.class, XmlType.class);
        assertAnnotationTypes(union.getDeclaredAnnotations(), XmlRootElement.class, XmlType.class);

        assertEquals("root-a", union.getAnnotation(XmlRootElement.class).name());

        assertTrue(union.isAnnotationPresent(XmlRootElement.class));
        assertTrue(union.isAnnotationPresent(XmlType.class));
    }
}
