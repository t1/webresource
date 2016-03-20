package com.github.t1.webresource.tools;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * This is by far not complete.
 *
 * @see "https://en.wikipedia.org/wiki/English_plurals"
 */
public class PluralizeTest {
    @Test
    public void shouldPluralize() {
        String pluralized = StringTool.pluralize("test");

        assertThat(pluralized).isEqualTo("tests");
    }

    @Test
    public void shouldPluralizeWithConsonantAndY() {
        String pluralized = StringTool.pluralize("fly");

        assertThat(pluralized).isEqualTo("flies");
    }

    @Test
    public void shouldPluralizeWithVocalAndY() {
        String pluralized = StringTool.pluralize("key");

        assertThat(pluralized).isEqualTo("keys");
    }

    @Test
    public void shouldPluralizeWithS() {
        String pluralized = StringTool.pluralize("kiss");

        assertThat(pluralized).isEqualTo("kisses");
    }
}
