package com.github.t1.webresource;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.*;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.persistence.Id;

import org.hamcrest.CoreMatchers;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class WebResourceFieldTest {
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

    private WebResourceField findField() {
        return WebResourceField.findField(type, Id.class.getName());
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldParseString() throws Exception {
        mockFieldType(field, "java.lang.String");

        WebResourceField idType = findField();

        assertNull(idType.packageImport());
        assertEquals("String", idType.type());
        assertEquals("id", idType.name);
        assertTrue(idType.nullable());
    }

    @Test
    public void shouldParseLong() throws Exception {
        mockFieldType(field, "java.lang.Long");

        WebResourceField idType = findField();

        assertNull(idType.packageImport());
        assertEquals("Long", idType.type());
        assertEquals("id", idType.name);
        assertTrue(idType.nullable());
    }

    @Test
    public void shouldParsePrimitiveLong() throws Exception {
        mockFieldType(field, "long");

        WebResourceField idType = findField();

        assertNull(idType.packageImport());
        assertEquals("long", idType.type());
        assertEquals("id", idType.name);
        assertFalse(idType.nullable());
    }

    @Test
    public void shouldParseInteger() throws Exception {
        mockFieldType(field, "java.lang.Integer");

        WebResourceField idType = findField();

        assertNull(idType.packageImport());
        assertEquals("Integer", idType.type());
        assertEquals("id", idType.name);
        assertTrue(idType.nullable());
    }

    @Test
    public void shouldParsePrimitiveInt() throws Exception {
        mockFieldType(field, "int");

        WebResourceField idType = findField();

        assertNull(idType.packageImport());
        assertEquals("int", idType.type());
        assertEquals("id", idType.name);
        assertFalse(idType.nullable());
    }

    @Test
    public void shouldParseShort() throws Exception {
        mockFieldType(field, "java.lang.Short");

        WebResourceField idType = findField();

        assertNull(idType.packageImport());
        assertEquals("Short", idType.type());
        assertEquals("id", idType.name);
        assertTrue(idType.nullable());
    }

    @Test
    public void shouldParsePrimitiveShort() throws Exception {
        mockFieldType(field, "short");

        WebResourceField idType = findField();

        assertNull(idType.packageImport());
        assertEquals("short", idType.type());
        assertEquals("id", idType.name);
        assertFalse(idType.nullable());
    }

    @Test
    public void shouldParseDouble() throws Exception {
        mockFieldType(field, "java.lang.Double");

        WebResourceField idType = findField();

        assertNull(idType.packageImport());
        assertEquals("Double", idType.type());
        assertEquals("id", idType.name);
        assertTrue(idType.nullable());
    }

    @Test
    public void shouldParsePrimitiveDouble() throws Exception {
        mockFieldType(field, "double");

        WebResourceField idType = findField();

        assertNull(idType.packageImport());
        assertEquals("double", idType.type());
        assertEquals("id", idType.name);
        assertFalse(idType.nullable());
    }

    @Test
    public void shouldParseBigInteger() throws Exception {
        mockFieldType(field, "java.math.BigInteger");

        WebResourceField idType = findField();

        assertEquals("java.math.BigInteger", idType.packageImport());
        assertEquals("BigInteger", idType.type());
        assertEquals("id", idType.name);
        assertTrue(idType.nullable());
    }

    @Test
    public void shouldParseBigDecimal() throws Exception {
        mockFieldType(field, "java.math.BigDecimal");

        WebResourceField idType = findField();

        assertEquals("java.math.BigDecimal", idType.packageImport());
        assertEquals("BigDecimal", idType.type());
        assertEquals("id", idType.name);
        assertTrue(idType.nullable());
    }

    @Test
    public void shouldParseSqlDate() throws Exception {
        mockFieldType(field, "java.sql.Date");

        WebResourceField idType = findField();

        assertEquals("java.sql.Date", idType.packageImport());
        assertEquals("Date", idType.type());
        assertEquals("id", idType.name);
        assertTrue(idType.nullable());
    }

    @Test
    public void shouldParseUtilDate() throws Exception {
        mockFieldType(field, "java.util.Date");

        WebResourceField idType = findField();

        assertEquals("java.util.Date", idType.packageImport());
        assertEquals("Date", idType.type());
        assertEquals("id", idType.name);
        assertTrue(idType.nullable());
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

        WebResourceField idType = findField();

        assertNull(idType.packageImport());
        assertEquals("long", idType.type());
        assertEquals("id", idType.name);
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

        WebResourceField idType = findField();

        assertEquals("java.math.BigDecimal", idType.packageImport());
        assertEquals("BigDecimal", idType.type());
        assertEquals("id", idType.name);
        assertTrue(idType.nullable());
    }

    @Test
    public void shouldFailWithTwoIdFields() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(CoreMatchers.startsWith("more than one javax.persistence.Id field found: "));

        mockFieldType(field, "long");

        Element field2 = mockField();
        mockFieldType(field2, "java.math.BigDecimal");
        fields.add(field2);

        findField();
    }
}
