package com.github.t1.webresource.tools;

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
 * be instantiated for each run. E.g. {@link CamelToDelimiter} needs to remember if it's the first character, so no leading space
 * is produced.
 */
@RequiredArgsConstructor
public class StringTool implements Function<String, String> {
    // ----------------- core

    public static StringTool empty() {
        return of(IntFunction_identityStream());
    }

    public static StringTool of(IntFunction<? extends IntStream> function) {
        return new StringTool(function, Function.identity());
    }

    @SafeVarargs
    public static StringTool of(IntFunction<? extends IntStream> first, IntFunction<? extends IntStream>... rest) {
        StringTool tool = StringTool.of(first);
        for (IntFunction<? extends IntStream> function : rest)
            tool = tool.and(function);
        return tool;
    }

    public StringTool and(IntFunction<? extends IntStream> function) {
        return new StringTool(IntFunction_andThen(this.function, function), finisher);
    }

    public StringTool and(Function<String, String> finisher) {
        return new StringTool(this.function, this.finisher.andThen(finisher));
    }

    private final IntFunction<? extends IntStream> function;
    private final Function<String, String> finisher;

    @Override public String apply(String in) {
        StringBuilder out = new StringBuilder();
        in.codePoints().flatMap(function).forEach(out::appendCodePoint);
        return finisher.apply(out.toString());
    }

    // ----------------- helpers

    private static IntFunction<? extends IntStream> IntFunction_andThen(
            IntFunction<? extends IntStream> before,
            IntFunction<? extends IntStream> after) {
        return codePoint -> before.apply(codePoint).flatMap(after);
    }

    private static IntFunction<? extends IntStream> IntFunction_identityStream() { return IntStream::of; }

    // ----------------- functions

    public static IntStream uppercase(int codePoint) { return IntStream.of(Character.toUpperCase(codePoint)); }

    public static IntStream lowercase(int codePoint) { return IntStream.of(Character.toLowerCase(codePoint)); }

    public static CamelToDelimiter camelToKebabCase() { return new CamelToDelimiter('-'); }

    public static CamelToDelimiter camelToWords() { return new CamelToDelimiter(' '); }

    @RequiredArgsConstructor
    public static class CamelToDelimiter implements IntFunction<IntStream> {
        private final char delimiter;

        private boolean first = true;

        @Override public IntStream apply(int codePoint) {
            try {
                if (!first && Character.isUpperCase(codePoint)) {
                    return IntStream.of(delimiter, codePoint);
                } else
                    return IntStream.of(codePoint);
            } finally {
                first = false;
            }
        }
    }

    /** This works only for very common and very regular cases. */
    public static String pluralize(String string) {
        if (string.endsWith("y") && isConsonant(string.charAt(string.length() - 2)))
            return string.substring(0, string.length() - 1) + "ies";
        if (string.endsWith("s"))
            return string + "es";
        return string + "s";
    }

    private static boolean isConsonant(char c) {
        //noinspection SpellCheckingInspection
        return "bcdfghjklmnpqrstvwxyz".indexOf(Character.toLowerCase(c)) >= 0;
    }
}
