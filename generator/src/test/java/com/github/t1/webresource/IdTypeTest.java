package com.github.t1.webresource;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class IdTypeTest {
    @Mock
    TypeElement type;

    @SuppressWarnings("unchecked")
    private void mockFieldType(String fieldType) {
        Element field = mock(Element.class);
        when((List<Element>) type.getEnclosedElements()).thenReturn(Arrays.asList(field));

        when(field.getKind()).thenReturn(ElementKind.FIELD);
        TypeMirror fieldTypeMirror = mock(TypeMirror.class);
        when(field.asType()).thenReturn(fieldTypeMirror);

        when(fieldTypeMirror.toString()).thenReturn(fieldType);
        AnnotationMirror annotationMirror = mock(AnnotationMirror.class);
        when((List<AnnotationMirror>) field.getAnnotationMirrors()).thenReturn(Arrays.asList(annotationMirror));

        DeclaredType declaredAnnotationType = mock(DeclaredType.class);
        when(annotationMirror.getAnnotationType()).thenReturn(declaredAnnotationType);
        when(declaredAnnotationType.toString()).thenReturn("javax.persistence.Id");
    }

    @Test
    public void shouldParseString() throws Exception {
        mockFieldType("java.lang.String");

        IdType idType = new IdType(type);

        assertNull(idType.packageImport);
        assertEquals("String", idType.simpleName);
        assertTrue(idType.nullable);
    }

    @Test
    public void shouldParseLong() throws Exception {
        mockFieldType("java.lang.Long");

        IdType idType = new IdType(type);

        assertNull(idType.packageImport);
        assertEquals("Long", idType.simpleName);
        assertTrue(idType.nullable);
    }

    @Test
    public void shouldParsePrimitiveLong() throws Exception {
        mockFieldType("long");

        IdType idType = new IdType(type);

        assertNull(idType.packageImport);
        assertEquals("long", idType.simpleName);
        assertFalse(idType.nullable);
    }

    @Test
    public void shouldParseInteger() throws Exception {
        mockFieldType("java.lang.Integer");

        IdType idType = new IdType(type);

        assertNull(idType.packageImport);
        assertEquals("Integer", idType.simpleName);
        assertTrue(idType.nullable);
    }

    @Test
    public void shouldParsePrimitiveInt() throws Exception {
        mockFieldType("int");

        IdType idType = new IdType(type);

        assertNull(idType.packageImport);
        assertEquals("int", idType.simpleName);
        assertFalse(idType.nullable);
    }

    @Test
    public void shouldParseShort() throws Exception {
        mockFieldType("java.lang.Short");

        IdType idType = new IdType(type);

        assertNull(idType.packageImport);
        assertEquals("Short", idType.simpleName);
        assertTrue(idType.nullable);
    }

    @Test
    public void shouldParsePrimitiveShort() throws Exception {
        mockFieldType("short");

        IdType idType = new IdType(type);

        assertNull(idType.packageImport);
        assertEquals("short", idType.simpleName);
        assertFalse(idType.nullable);
    }

    @Test
    public void shouldParseDouble() throws Exception {
        mockFieldType("java.lang.Double");

        IdType idType = new IdType(type);

        assertNull(idType.packageImport);
        assertEquals("Double", idType.simpleName);
        assertTrue(idType.nullable);
    }

    @Test
    public void shouldParsePrimitiveDouble() throws Exception {
        mockFieldType("double");

        IdType idType = new IdType(type);

        assertNull(idType.packageImport);
        assertEquals("double", idType.simpleName);
        assertFalse(idType.nullable);
    }

    @Test
    public void shouldParseBigInteger() throws Exception {
        mockFieldType("java.math.BigInteger");

        IdType idType = new IdType(type);

        assertEquals("java.math.BigInteger", idType.packageImport);
        assertEquals("BigInteger", idType.simpleName);
        assertTrue(idType.nullable);
    }

    @Test
    public void shouldParseBigDecimal() throws Exception {
        mockFieldType("java.math.BigDecimal");

        IdType idType = new IdType(type);

        assertEquals("java.math.BigDecimal", idType.packageImport);
        assertEquals("BigDecimal", idType.simpleName);
        assertTrue(idType.nullable);
    }

    @Test
    public void shouldParseSqlDate() throws Exception {
        mockFieldType("java.sql.Date");

        IdType idType = new IdType(type);

        assertEquals("java.sql.Date", idType.packageImport);
        assertEquals("Date", idType.simpleName);
        assertTrue(idType.nullable);
    }

    @Test
    public void shouldParseUtilDate() throws Exception {
        mockFieldType("java.util.Date");

        IdType idType = new IdType(type);

        assertEquals("java.util.Date", idType.packageImport);
        assertEquals("Date", idType.simpleName);
        assertTrue(idType.nullable);
    }
}
