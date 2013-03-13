package com.github.t1.webresource;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.*;

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

    static void mockFieldType(Element field, String fieldType, String fieldName) {
        TypeMirror fieldTypeMirror = mock(TypeMirror.class);
        when(field.asType()).thenReturn(fieldTypeMirror);
        when(field.getSimpleName()).thenReturn(new NameMock(fieldName));

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
    private final List<Element> fields = new ArrayList<Element>();

    @Before
    public void before() {
        fields.add(field);
        doReturn(fields).when(type).getEnclosedElements();
    }

    @Test
    public void shouldParseString() throws Exception {
        mockFieldType(field, "java.lang.String", "id");

        IdType idType = IdType.of(type);

        assertNull(idType.packageImport());
        assertEquals("String", idType.toString());
        assertEquals("id", idType.fieldName());
        assertTrue(idType.nullable());
    }

    @Test
    public void shouldParseLong() throws Exception {
        mockFieldType(field, "java.lang.Long", "id");

        IdType idType = IdType.of(type);

        assertNull(idType.packageImport());
        assertEquals("Long", idType.toString());
        assertEquals("id", idType.fieldName());
        assertTrue(idType.nullable());
    }

    @Test
    public void shouldParsePrimitiveLong() throws Exception {
        mockFieldType(field, "long", "id");

        IdType idType = IdType.of(type);

        assertNull(idType.packageImport());
        assertEquals("long", idType.toString());
        assertEquals("id", idType.fieldName());
        assertFalse(idType.nullable());
    }

    @Test
    public void shouldParseInteger() throws Exception {
        mockFieldType(field, "java.lang.Integer", "id");

        IdType idType = IdType.of(type);

        assertNull(idType.packageImport());
        assertEquals("Integer", idType.toString());
        assertEquals("id", idType.fieldName());
        assertTrue(idType.nullable());
    }

    @Test
    public void shouldParsePrimitiveInt() throws Exception {
        mockFieldType(field, "int", "id");

        IdType idType = IdType.of(type);

        assertNull(idType.packageImport());
        assertEquals("int", idType.toString());
        assertEquals("id", idType.fieldName());
        assertFalse(idType.nullable());
    }

    @Test
    public void shouldParseShort() throws Exception {
        mockFieldType(field, "java.lang.Short", "id");

        IdType idType = IdType.of(type);

        assertNull(idType.packageImport());
        assertEquals("Short", idType.toString());
        assertEquals("id", idType.fieldName());
        assertTrue(idType.nullable());
    }

    @Test
    public void shouldParsePrimitiveShort() throws Exception {
        mockFieldType(field, "short", "id");

        IdType idType = IdType.of(type);

        assertNull(idType.packageImport());
        assertEquals("short", idType.toString());
        assertEquals("id", idType.fieldName());
        assertFalse(idType.nullable());
    }

    @Test
    public void shouldParseDouble() throws Exception {
        mockFieldType(field, "java.lang.Double", "id");

        IdType idType = IdType.of(type);

        assertNull(idType.packageImport());
        assertEquals("Double", idType.toString());
        assertEquals("id", idType.fieldName());
        assertTrue(idType.nullable());
    }

    @Test
    public void shouldParsePrimitiveDouble() throws Exception {
        mockFieldType(field, "double", "id");

        IdType idType = IdType.of(type);

        assertNull(idType.packageImport());
        assertEquals("double", idType.toString());
        assertEquals("id", idType.fieldName());
        assertFalse(idType.nullable());
    }

    @Test
    public void shouldParseBigInteger() throws Exception {
        mockFieldType(field, "java.math.BigInteger", "id");

        IdType idType = IdType.of(type);

        assertEquals("java.math.BigInteger", idType.packageImport());
        assertEquals("BigInteger", idType.toString());
        assertEquals("id", idType.fieldName());
        assertTrue(idType.nullable());
    }

    @Test
    public void shouldParseBigDecimal() throws Exception {
        mockFieldType(field, "java.math.BigDecimal", "id");

        IdType idType = IdType.of(type);

        assertEquals("java.math.BigDecimal", idType.packageImport());
        assertEquals("BigDecimal", idType.toString());
        assertEquals("id", idType.fieldName());
        assertTrue(idType.nullable());
    }

    @Test
    public void shouldParseSqlDate() throws Exception {
        mockFieldType(field, "java.sql.Date", "id");

        IdType idType = IdType.of(type);

        assertEquals("java.sql.Date", idType.packageImport());
        assertEquals("Date", idType.toString());
        assertEquals("id", idType.fieldName());
        assertTrue(idType.nullable());
    }

    @Test
    public void shouldParseUtilDate() throws Exception {
        mockFieldType(field, "java.util.Date", "id");

        IdType idType = IdType.of(type);

        assertEquals("java.util.Date", idType.packageImport());
        assertEquals("Date", idType.toString());
        assertEquals("id", idType.fieldName());
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
        mockFieldType(field2, "long", "id");

        IdType idType = IdType.of(type);

        assertNull(idType.packageImport());
        assertEquals("long", idType.toString());
        assertEquals("id", idType.fieldName());
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
        mockFieldType(field2, "java.math.BigDecimal", "id");

        IdType idType = IdType.of(type);

        assertEquals("java.math.BigDecimal", idType.packageImport());
        assertEquals("BigDecimal", idType.toString());
        assertEquals("id", idType.fieldName());
        assertTrue(idType.nullable());
    }

    @Test
    public void shouldPreferWebResourceKey() throws Exception {
        mockFieldType(field, "long", "id");

        Element field2 = mockField();
        fields.add(field2);
        TypeMirror fieldTypeMirror = mock(TypeMirror.class);
        when(field2.asType()).thenReturn(fieldTypeMirror);
        when(field2.getSimpleName()).thenReturn(new NameMock("key"));

        when(fieldTypeMirror.toString()).thenReturn("java.lang.String");
        AnnotationMirror annotationMirror = mock(AnnotationMirror.class);
        doReturn(Arrays.asList(annotationMirror)).when(field2).getAnnotationMirrors();

        DeclaredType declaredAnnotationType = mock(DeclaredType.class);
        when(annotationMirror.getAnnotationType()).thenReturn(declaredAnnotationType);
        when(declaredAnnotationType.toString()).thenReturn(WebResourceKey.class.getName());

        IdType idType = IdType.of(type);

        assertNull(idType.packageImport());
        assertEquals("String", idType.toString());
        assertEquals("key", idType.fieldName());
        assertTrue(idType.nullable());
    }
}
