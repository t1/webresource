package com.github.t1.webresource.meta;

import static org.junit.Assert.*;

import org.junit.*;
import org.junit.rules.ExpectedException;

public class ConverterTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldConvertStringToString() {
        Converter<String> converter = Converter.to(String.class);

        String result = converter.convert("test");

        assertEquals("test", result);
    }

    @Test
    public void shouldConvertIntegerToNumber() {
        Converter<Number> converter = Converter.to(Number.class);

        Number result = converter.convert(123);

        assertEquals(123, result);
    }

    @Test
    public void shouldConvertWithValueOfMethod() {
        Converter<Integer> converter = Converter.to(Integer.class);

        int result = converter.convert("123");

        assertEquals(123, result);
    }

    public static class Dummy {}

    @Test
    public void shouldNotConvertWithoutValueOfMethod() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("can't convert [123] to Dummy");

        Converter<Dummy> converter = Converter.to(Dummy.class);

        converter.convert("123");
    }

    public static class NonStaticValueOf {
        public NonStaticValueOf valueOf(@SuppressWarnings("unused") String string) {
            throw new UnsupportedOperationException();
        }
    }

    @Test
    public void shouldNotConvertWithNonStaticValueOfMethod() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("can't convert [123] to NonStaticValueOf");

        Converter<NonStaticValueOf> converter = Converter.to(NonStaticValueOf.class);

        converter.convert("123");
    }

    public static class NoArgsValueOf {
        public NoArgsValueOf valueOf() {
            throw new UnsupportedOperationException();
        }
    }

    @Test
    public void shouldNotConvertWithNoArgsValueOfMethod() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("can't convert [123] to NoArgsValueOf");

        Converter<NoArgsValueOf> converter = Converter.to(NoArgsValueOf.class);

        converter.convert("123");
    }


    public static class TwoArgsValueOf {
        @SuppressWarnings("unused")
        public static NonStaticValueOf valueOf(String string1, String string2) {
            throw new UnsupportedOperationException();
        }
    }

    @Test
    public void shouldNotConvertWithTwoArgsValueOfMethod() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("can't convert [123] to TwoArgsValueOf");

        Converter<TwoArgsValueOf> converter = Converter.to(TwoArgsValueOf.class);

        converter.convert("123");
    }

    public static class NumberValueOf {
        public final Number number;

        public NumberValueOf(Number number) {
            this.number = number;
        }

        public static NumberValueOf valueOf(Number number) {
            return new NumberValueOf(number);
        }
    }

    @Test
    public void shouldConvertSuperclassValueOfMethod() {
        Converter<NumberValueOf> converter = Converter.to(NumberValueOf.class);

        NumberValueOf converted = converter.convert(123);

        assertEquals(123, converted.number);
    }
}
