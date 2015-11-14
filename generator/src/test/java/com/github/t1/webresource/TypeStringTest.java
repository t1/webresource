package com.github.t1.webresource;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.github.t1.webresource.typewriter.TypeString;

public class TypeStringTest {
    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWithInvalidTypeString() {
        new TypeString("-");
    }

    @Test
    public void shouldParsePrimitiveInt() {
        TypeString type = new TypeString("int");

        assertFalse(type.nullable);
        assertEquals("int", type.simple);
        assertEquals(emptyList(), type.imports);
    }

    @Test
    public void shouldParseBoxedIntegerWithoutImport() {
        TypeString type = new TypeString("java.lang.Integer");

        assertTrue(type.nullable);
        assertEquals("Integer", type.simple);
        assertEquals(emptyList(), type.imports);
    }

    @Test
    public void shouldParseStringWithoutImport() {
        TypeString type = new TypeString("java.lang.String");

        assertTrue(type.nullable);
        assertEquals("String", type.simple);
        assertEquals(emptyList(), type.imports);
    }

    @Test
    public void shouldParseWithImport() {
        TypeString type = new TypeString("java.math.BigInteger");

        assertTrue(type.nullable);
        assertEquals("BigInteger", type.simple);
        assertEquals(singletonList("java.math.BigInteger"), type.imports);
    }

    @Test
    public void shouldParseGenericTypeWithElementImports() {
        TypeString type = new TypeString("java.util.List<java.math.BigInteger>");

        assertTrue(type.nullable);
        assertEquals("List<BigInteger>", type.generic);
        assertEquals(asList("java.util.List", "java.math.BigInteger"), type.imports);
    }

    @Test
    public void shouldParseGenericTypeWithoutElementImports() {
        TypeString type = new TypeString("java.util.List<java.lang.Integer>");

        assertTrue(type.nullable);
        assertEquals("List<Integer>", type.generic);
        assertEquals(singletonList("java.util.List"), type.imports);
    }

    @Test
    public void shouldParseTwoElementGenericTypeWithOneElementImports() {
        TypeString type = new TypeString("java.util.Map<java.math.BigInteger, java.lang.String>");

        assertTrue(type.nullable);
        assertEquals("Map<BigInteger, String>", type.generic);
        assertEquals(asList("java.util.Map", "java.math.BigInteger"), type.imports);
    }

    @Test
    public void shouldParseTwoElementGenericTypeWithTwoElementImports() {
        TypeString type = new TypeString("java.util.Map<java.math.BigInteger, java.math.BigDecimal>");

        assertTrue(type.nullable);
        assertEquals("Map<BigInteger, BigDecimal>", type.generic);
        assertEquals(asList("java.util.Map", "java.math.BigInteger", "java.math.BigDecimal"), type.imports);
    }
}
