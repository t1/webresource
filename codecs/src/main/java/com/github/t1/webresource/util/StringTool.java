package com.github.t1.webresource.util;

import lombok.RequiredArgsConstructor;

import java.util.function.*;
import java.util.stream.IntStream;

/**
 * Compose-able String functions, iterating the string only once, even if the transformation is composed of several functions.
 *
 * As functions may return no, one, or more output characters for every input character, they return {@link IntStream} that
 * get {@link IntStream#flatMap(IntFunction) flat mapped}.
 *
 * Functions without state can be just method references (e.g. {@link #uppercase(int)}; functions that need state have to
 * be instantiated for each run. E.g. {@link CamelToWords} needs to remember if it's the first character, so no leading space
 * is produced.
 */
@RequiredArgsConstructor
public class StringTool implements Function<String, String> {
    // ----------------- static convenience
    public static String camelToWords(String camel) {
        return StringTool.of(new CamelToWords<IntStream>()).apply(camel);
    }

    // ----------------- core

    public static StringTool of(IntFunction<? extends IntStream> function) {
        return new StringTool(function);
    }

    public StringTool and(IntFunction<? extends IntStream> function) {
        return new StringTool(IntFunction_andThen(this.function, function));
    }

    private final IntFunction<? extends IntStream> function;

    @Override public String apply(String in) {
        StringBuilder out = new StringBuilder();
        in.codePoints().flatMap(function).forEach(out::appendCodePoint);
        return out.toString();
    }

    // ----------------- functions

    public static IntStream uppercase(int codePoint) {
        return IntStream.of(Character.toUpperCase(codePoint));
    }

    public static class CamelToWords<T> implements IntFunction<IntStream> {
        private boolean first = true;

        @Override public IntStream apply(int codePoint) {
            try {
                if (!first && Character.isUpperCase(codePoint))
                    return IntStream.of(' ', codePoint);
                else
                    return IntStream.of(codePoint);
            } finally {
                first = false;
            }
        }
    }

    // ----------------- helpers

    public static IntFunction<? extends IntStream> IntFunction_andThen(
            IntFunction<? extends IntStream> before,
            IntFunction<? extends IntStream> after) {
        return codePoint -> before.apply(codePoint).flatMap(after);
    }

    public static IntFunction<? extends IntStream> IntFunction_identityStream() { return IntStream::of; }
}
