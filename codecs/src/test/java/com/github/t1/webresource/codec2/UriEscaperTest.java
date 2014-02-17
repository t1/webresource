package com.github.t1.webresource.codec2;

import static org.junit.Assert.*;

import java.net.URI;

import org.junit.Test;

public class UriEscaperTest {
    UriEscaper escaper = new UriEscaper();

    @Test
    public void shouldResolvePathWithSlashes() throws Exception {
        URI resolved = escaper.escape("path/with/slashes");

        assertEquals("path/with/slashes", resolved.toASCIIString());
    }

    @Test
    public void shouldUrlEscapeSpaces() throws Exception {
        URI resolved = escaper.escape("this contains spaces");

        assertEquals("this%20contains%20spaces", resolved.toASCIIString());
    }

    @Test
    public void shouldUrlEscapeHash() throws Exception {
        URI resolved = escaper.escape("#");

        assertEquals("%23", resolved.toASCIIString());
    }

    @Test
    public void shouldUrlEscapeCurlies() throws Exception {
        URI resolved = escaper.escape("{}");

        assertEquals("%7B%7D", resolved.toASCIIString());
    }

    @Test
    public void shouldUrlNotEscapeReserved() throws Exception {
        URI resolved = escaper.escape(";/?:@&=+$,");

        assertEquals(";/?:@&=+$,", resolved.toASCIIString());
    }

    @Test
    public void shouldUrlEscapeOpenBrackets() throws Exception {
        URI resolved = escaper.escape("a[b[c");

        assertEquals("a%5Bb%5Bc", resolved.toASCIIString());
    }

    @Test
    public void shouldUrlEscapeCloseBrackets() throws Exception {
        URI resolved = escaper.escape("a]b]c");

        assertEquals("a%5Db%5Dc", resolved.toASCIIString());
    }

    @Test
    public void shouldUrlEscapeLeadingOpenBrackets() throws Exception {
        URI resolved = escaper.escape("[x");

        assertEquals("%5Bx", resolved.toASCIIString());
    }

    @Test
    public void shouldUrlEscapeLeadingCloseBrackets() throws Exception {
        URI resolved = escaper.escape("]x");

        assertEquals("%5Dx", resolved.toASCIIString());
    }

    @Test
    public void shouldUrlEscapeLeadingCloseOpenBrackets() throws Exception {
        URI resolved = escaper.escape("][");

        assertEquals("%5D%5B", resolved.toASCIIString());
    }

    @Test
    public void shouldUrlEscapeOpenCloseBrackets() throws Exception {
        URI resolved = escaper.escape("a[b]c");

        assertEquals("a%5Bb%5Dc", resolved.toASCIIString());
    }

    @Test
    public void shouldUrlEscapeHashWithinOpenCloseBrackets() throws Exception {
        URI resolved = escaper.escape("[#]");

        assertEquals("%5B%23%5D", resolved.toASCIIString());
    }
}
