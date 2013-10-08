package com.github.t1.webresource.codec;

import static org.junit.Assert.*;

import java.io.*;

import lombok.Data;

import org.junit.Test;

public class FormUrlDecoderTest {

    private <T> T decode(Class<T> type, String encoded) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(encoded));

        return new FormUrlDecoder<>(type).read(reader);
    }

    static class PrivateConstructorPojo {
        private PrivateConstructorPojo() {}

        public String trait = "aaa";
    }

    @Test
    public void shouldDecodingPrivateConstructor() throws Exception {
        PrivateConstructorPojo result = decode(PrivateConstructorPojo.class, "trait=xxx");

        assertEquals("xxx", result.trait);
    }

    @Data
    static class StringPojo {
        String one = "aaa";
        String two = "bbb";
    }

    @Test
    public void shouldLeaveAsItIsWhenDecodingEmpty() throws Exception {
        StringPojo result = decode(StringPojo.class, "");

        assertEquals("aaa", result.one);
        assertEquals("bbb", result.two);
    }

    @Test
    public void shouldOverrideOneWhenDecodingOne() throws Exception {
        StringPojo result = decode(StringPojo.class, "one=xxx");

        assertEquals("xxx", result.one);
        assertEquals("bbb", result.two);
    }

    @Test
    public void shouldOverrideTwoWhenDecodingTwo() throws Exception {
        StringPojo result = decode(StringPojo.class, "one=xxx&two=yyy");

        assertEquals("xxx", result.one);
        assertEquals("yyy", result.two);
    }
}
