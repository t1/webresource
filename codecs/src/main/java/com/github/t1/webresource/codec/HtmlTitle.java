package com.github.t1.webresource.codec;

import com.github.t1.webresource.util.StringTool;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.*;

/**
 * Sets the title for the pojo for the `<head><title>` as well as the `h1`.
 */
@Retention(RUNTIME)
public @interface HtmlTitle {
    /**
     * Used for mappings; defaults to {@link StringTool#camelToWords()} to the pojo's simple class name.
     * If the pojo is a {@link java.util.Collection collection} and the generic type information is available
     * (i.e. for the root pojo) then the simple class name of the collection element pojo is taken as default.
     */
    String value() default "";

    /**
     * Used for collections; defaults to applying {@link com.github.t1.webresource.util.StringTool#pluralize(String)}
     * to the {@link #value()}.
     */
    String plural() default "";
}
