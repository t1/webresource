package com.github.t1.webresource.codec2;

import static org.junit.Assert.*;

import java.net.URI;

import org.junit.Test;

public class UriEscaperTest {
    UriEscaper escaper = new UriEscaper();

    @Test
    public void shouldResolvePathWithSlashes() {
        URI resolved = escaper.escape("path/with/slashes");

        assertEquals("path/with/slashes", resolved.toASCIIString());
    }

    @Test
    public void shouldUrlEscapeSpaces() {
        URI resolved = escaper.escape("this contains spaces");

        assertEquals("this%20contains%20spaces", resolved.toASCIIString());
    }

    @Test
    public void shouldUrlEscapeHash() {
        URI resolved = escaper.escape("#");

        assertEquals("%23", resolved.toASCIIString());
    }

    @Test
    public void shouldUrlEscapeCurlies() {
        URI resolved = escaper.escape("{}");

        assertEquals("%7B%7D", resolved.toASCIIString());
    }

    @Test
    public void shouldUrlNotEscapeReserved() {
        URI resolved = escaper.escape(";/?:@&=+$,");

        assertEquals(";/?:@&=+$,", resolved.toASCIIString());
    }

    @Test
    public void shouldUrlEscapeOpenBrackets() {
        URI resolved = escaper.escape("a[b[c");

        assertEquals("a%5Bb%5Bc", resolved.toASCIIString());
    }

    @Test
    public void shouldUrlEscapeCloseBrackets() {
        URI resolved = escaper.escape("a]b]c");

        assertEquals("a%5Db%5Dc", resolved.toASCIIString());
    }

    @Test
    public void shouldUrlEscapeLeadingOpenBrackets() {
        URI resolved = escaper.escape("[x");

        assertEquals("%5Bx", resolved.toASCIIString());
    }

    @Test
    public void shouldUrlEscapeLeadingCloseBrackets() {
        URI resolved = escaper.escape("]x");

        assertEquals("%5Dx", resolved.toASCIIString());
    }

    @Test
    public void shouldUrlEscapeLeadingCloseOpenBrackets() {
        URI resolved = escaper.escape("][");

        assertEquals("%5D%5B", resolved.toASCIIString());
    }

    @Test
    public void shouldUrlEscapeOpenCloseBrackets() {
        URI resolved = escaper.escape("a[b]c");

        assertEquals("a%5Bb%5Dc", resolved.toASCIIString());
    }

    @Test
    public void shouldUrlEscapeHashWithinOpenCloseBrackets() {
        URI resolved = escaper.escape("[#]");

        assertEquals("%5B%23%5D", resolved.toASCIIString());
    }
}
