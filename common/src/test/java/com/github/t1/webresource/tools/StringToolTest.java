package com.github.t1.webresource.tools;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class StringToolTest {
    @Test
    public void shouldPluralize() {
        String pluralized = StringTool.pluralize("test");

        assertThat(pluralized).isEqualTo("tests");
    }

    @Test
    public void shouldPluralizeWithY() {
        String pluralized = StringTool.pluralize("fly");

        assertThat(pluralized).isEqualTo("flies");
    }
}
