package com.github.t1.webresource;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class IdTypeTest {
    static Element mockField() {
        Element field = mock(Element.class);
        when(field.getKind()).thenReturn(ElementKind.FIELD);
        return field;
    }

    static void mockFieldType(Element field, String fieldType) {
        TypeMirror fieldTypeMirror = mock(TypeMirror.class);
        when(field.asType()).thenReturn(fieldTypeMirror);

        when(fieldTypeMirror.toString()).thenReturn(fieldType);
        AnnotationMirror annotationMirror = mock(AnnotationMirror.class);
        doReturn(Arrays.asList(annotationMirror)).when(field).getAnnotationMirrors();

        DeclaredType declaredAnnotationType = mock(DeclaredType.class);
        when(annotationMirror.getAnnotationType()).thenReturn(declaredAnnotationType);
        when(declaredAnnotationType.toString()).thenReturn("javax.persistence.Id");
    }

    @Mock
    TypeElement type;
    Element field = mockField();

    @Before
    public void before() {
        doReturn(Arrays.asList(field)).when(type).getEnclosedElements();
    }

    @Test
    public void shouldParseString() throws Exception {
        mockFieldType(field, "java.lang.String");

        IdType idType = IdType.of(type);

        assertNull(idType.packageImport());
        assertEquals("String", idType.toString());
        assertTrue(idType.nullable());
    }

    @Test
    public void shouldParseLong() throws Exception {
        mockFieldType(field, "java.lang.Long");

        IdType idType = IdType.of(type);

        assertNull(idType.packageImport());
        assertEquals("Long", idType.toString());
        assertTrue(idType.nullable());
    }

    @Test
    public void shouldParsePrimitiveLong() throws Exception {
        mockFieldType(field, "long");

        IdType idType = IdType.of(type);

        assertNull(idType.packageImport());
        assertEquals("long", idType.toString());
        assertFalse(idType.nullable());
    }

    @Test
    public void shouldParseInteger() throws Exception {
        mockFieldType(field, "java.lang.Integer");

        IdType idType = IdType.of(type);

        assertNull(idType.packageImport());
        assertEquals("Integer", idType.toString());
        assertTrue(idType.nullable());
    }

    @Test
    public void shouldParsePrimitiveInt() throws Exception {
        mockFieldType(field, "int");

        IdType idType = IdType.of(type);

        assertNull(idType.packageImport());
        assertEquals("int", idType.toString());
        assertFalse(idType.nullable());
    }

    @Test
    public void shouldParseShort() throws Exception {
        mockFieldType(field, "java.lang.Short");

        IdType idType = IdType.of(type);

        assertNull(idType.packageImport());
        assertEquals("Short", idType.toString());
        assertTrue(idType.nullable());
    }

    @Test
    public void shouldParsePrimitiveShort() throws Exception {
        mockFieldType(field, "short");

        IdType idType = IdType.of(type);

        assertNull(idType.packageImport());
        assertEquals("short", idType.toString());
        assertFalse(idType.nullable());
    }

    @Test
    public void shouldParseDouble() throws Exception {
        mockFieldType(field, "java.lang.Double");

        IdType idType = IdType.of(type);

        assertNull(idType.packageImport());
        assertEquals("Double", idType.toString());
        assertTrue(idType.nullable());
    }

    @Test
    public void shouldParsePrimitiveDouble() throws Exception {
        mockFieldType(field, "double");

        IdType idType = IdType.of(type);

        assertNull(idType.packageImport());
        assertEquals("double", idType.toString());
        assertFalse(idType.nullable());
    }

    @Test
    public void shouldParseBigInteger() throws Exception {
        mockFieldType(field, "java.math.BigInteger");

        IdType idType = IdType.of(type);

        assertEquals("java.math.BigInteger", idType.packageImport());
        assertEquals("BigInteger", idType.toString());
        assertTrue(idType.nullable());
    }

    @Test
    public void shouldParseBigDecimal() throws Exception {
        mockFieldType(field, "java.math.BigDecimal");

        IdType idType = IdType.of(type);

        assertEquals("java.math.BigDecimal", idType.packageImport());
        assertEquals("BigDecimal", idType.toString());
        assertTrue(idType.nullable());
    }

    @Test
    public void shouldParseSqlDate() throws Exception {
        mockFieldType(field, "java.sql.Date");

        IdType idType = IdType.of(type);

        assertEquals("java.sql.Date", idType.packageImport());
        assertEquals("Date", idType.toString());
        assertTrue(idType.nullable());
    }

    @Test
    public void shouldParseUtilDate() throws Exception {
        mockFieldType(field, "java.util.Date");

        IdType idType = IdType.of(type);

        assertEquals("java.util.Date", idType.packageImport());
        assertEquals("Date", idType.toString());
        assertTrue(idType.nullable());
    }

    @Test
    public void shouldReturnNullWithoutIdField() throws Exception {
        IdType idType = IdType.of(type);

        assertNull(idType);
    }

    @Test
    public void shouldInheritedPrimitiveLong() throws Exception {
        TypeElement parent = mock(TypeElement.class);
        DeclaredType parentMirror = mock(DeclaredType.class);
        when(parentMirror.asElement()).thenReturn(parent);
        when(type.getSuperclass()).thenReturn(parentMirror);

        Element field2 = mockField();
        doReturn(Arrays.asList(field2)).when(parent).getEnclosedElements();
        mockFieldType(field2, "long");

        IdType idType = IdType.of(type);

        assertNull(idType.packageImport());
        assertEquals("long", idType.toString());
        assertFalse(idType.nullable());
    }

    @Test
    public void shouldInheritedBigDecimal() throws Exception {
        TypeElement parent = mock(TypeElement.class);
        DeclaredType parentMirror = mock(DeclaredType.class);
        when(parentMirror.asElement()).thenReturn(parent);
        when(type.getSuperclass()).thenReturn(parentMirror);

        Element field2 = mockField();
        doReturn(Arrays.asList(field2)).when(parent).getEnclosedElements();
        mockFieldType(field2, "java.math.BigDecimal");

        IdType idType = IdType.of(type);

        assertEquals("java.math.BigDecimal", idType.packageImport());
        assertEquals("BigDecimal", idType.toString());
        assertTrue(idType.nullable());
    }
}