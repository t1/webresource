package com.github.t1.webresource;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.*;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.persistence.Id;

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
        mockFieldType(field, fieldType, "id", Id.class);
    }

    static void mockFieldType(Element field, String fieldType, String fieldName, Class<?> annotationType) {
        TypeMirror fieldTypeMirror = mock(TypeMirror.class);
        when(field.asType()).thenReturn(fieldTypeMirror);
        when(field.getSimpleName()).thenReturn(new NameMock(fieldName));

        when(fieldTypeMirror.toString()).thenReturn(fieldType);
        AnnotationMirror annotationMirror = mock(AnnotationMirror.class);
        doReturn(Arrays.asList(annotationMirror)).when(field).getAnnotationMirrors();

        DeclaredType declaredAnnotationType = mock(DeclaredType.class);
        when(annotationMirror.getAnnotationType()).thenReturn(declaredAnnotationType);
        when(declaredAnnotationType.toString()).thenReturn(annotationType.getName());
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
        mockFieldType(field, "java.lang.String");

        IdField idType = IdField.of(type);

        assertNull(idType.packageImport());
        assertEquals("String", idType.type());
        assertEquals("id", idType.name());
        assertTrue(idType.nullable());
        assertTrue(idType.primary());
    }

    @Test
    public void shouldParseLong() throws Exception {
        mockFieldType(field, "java.lang.Long");

        IdField idType = IdField.of(type);

        assertNull(idType.packageImport());
        assertEquals("Long", idType.type());
        assertEquals("id", idType.name());
        assertTrue(idType.nullable());
        assertTrue(idType.primary());
    }

    @Test
    public void shouldParsePrimitiveLong() throws Exception {
        mockFieldType(field, "long");

        IdField idType = IdField.of(type);

        assertNull(idType.packageImport());
        assertEquals("long", idType.type());
        assertEquals("id", idType.name());
        assertFalse(idType.nullable());
    }

    @Test
    public void shouldParseInteger() throws Exception {
        mockFieldType(field, "java.lang.Integer");

        IdField idType = IdField.of(type);

        assertNull(idType.packageImport());
        assertEquals("Integer", idType.type());
        assertEquals("id", idType.name());
        assertTrue(idType.nullable());
        assertTrue(idType.primary());
    }

    @Test
    public void shouldParsePrimitiveInt() throws Exception {
        mockFieldType(field, "int");

        IdField idType = IdField.of(type);

        assertNull(idType.packageImport());
        assertEquals("int", idType.type());
        assertEquals("id", idType.name());
        assertFalse(idType.nullable());
    }

    @Test
    public void shouldParseShort() throws Exception {
        mockFieldType(field, "java.lang.Short");

        IdField idType = IdField.of(type);

        assertNull(idType.packageImport());
        assertEquals("Short", idType.type());
        assertEquals("id", idType.name());
        assertTrue(idType.nullable());
        assertTrue(idType.primary());
    }

    @Test
    public void shouldParsePrimitiveShort() throws Exception {
        mockFieldType(field, "short");

        IdField idType = IdField.of(type);

        assertNull(idType.packageImport());
        assertEquals("short", idType.type());
        assertEquals("id", idType.name());
        assertFalse(idType.nullable());
    }

    @Test
    public void shouldParseDouble() throws Exception {
        mockFieldType(field, "java.lang.Double");

        IdField idType = IdField.of(type);

        assertNull(idType.packageImport());
        assertEquals("Double", idType.type());
        assertEquals("id", idType.name());
        assertTrue(idType.nullable());
        assertTrue(idType.primary());
    }

    @Test
    public void shouldParsePrimitiveDouble() throws Exception {
        mockFieldType(field, "double");

        IdField idType = IdField.of(type);

        assertNull(idType.packageImport());
        assertEquals("double", idType.type());
        assertEquals("id", idType.name());
        assertFalse(idType.nullable());
    }

    @Test
    public void shouldParseBigInteger() throws Exception {
        mockFieldType(field, "java.math.BigInteger");

        IdField idType = IdField.of(type);

        assertEquals("java.math.BigInteger", idType.packageImport());
        assertEquals("BigInteger", idType.type());
        assertEquals("id", idType.name());
        assertTrue(idType.nullable());
        assertTrue(idType.primary());
    }

    @Test
    public void shouldParseBigDecimal() throws Exception {
        mockFieldType(field, "java.math.BigDecimal");

        IdField idType = IdField.of(type);

        assertEquals("java.math.BigDecimal", idType.packageImport());
        assertEquals("BigDecimal", idType.type());
        assertEquals("id", idType.name());
        assertTrue(idType.nullable());
        assertTrue(idType.primary());
    }

    @Test
    public void shouldParseSqlDate() throws Exception {
        mockFieldType(field, "java.sql.Date");

        IdField idType = IdField.of(type);

        assertEquals("java.sql.Date", idType.packageImport());
        assertEquals("Date", idType.type());
        assertEquals("id", idType.name());
        assertTrue(idType.nullable());
        assertTrue(idType.primary());
    }

    @Test
    public void shouldParseUtilDate() throws Exception {
        mockFieldType(field, "java.util.Date");

        IdField idType = IdField.of(type);

        assertEquals("java.util.Date", idType.packageImport());
        assertEquals("Date", idType.type());
        assertEquals("id", idType.name());
        assertTrue(idType.nullable());
        assertTrue(idType.primary());
    }

    @Test
    public void shouldReturnNullWithoutIdField() throws Exception {
        IdField idType = IdField.of(type);

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

        IdField idType = IdField.of(type);

        assertNull(idType.packageImport());
        assertEquals("long", idType.type());
        assertEquals("id", idType.name());
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

        IdField idType = IdField.of(type);

        assertEquals("java.math.BigDecimal", idType.packageImport());
        assertEquals("BigDecimal", idType.type());
        assertEquals("id", idType.name());
        assertTrue(idType.nullable());
        assertTrue(idType.primary());
    }

    @Test
    public void shouldPreferWebResourceKey() throws Exception {
        mockFieldType(field, "long");

        Element field2 = mockField();
        fields.add(field2);
        mockFieldType(field2, "java.lang.String", "key", WebResourceKey.class);

        IdField idType = IdField.of(type);

        assertNull(idType.packageImport());
        assertEquals("String", idType.type());
        assertEquals("key", idType.name());
        assertTrue(idType.nullable());
        assertFalse(idType.primary());
    }
}
