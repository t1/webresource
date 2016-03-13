package com.github.t1.webresource.codec;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.*;

/**
 * Sets the title for the pojo for the `<head><title>` as well as the `h1`.
 */
@Retention(RUNTIME)
public @interface HtmlTitle {
    String value();

    /** Used for collections; defaults to applying {@link com.github.t1.webresource.util.StringTool#pluralize(String)} */
    String plural() default "";
}
