package com.github.t1.webresource.codec;

import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;

/**
 * The type attribute used for input fields. Defaults to <code>text</code> for most field types and
 * <code>checkbox</code> for booleans.
 */
@Retention(RUNTIME)
public @interface HtmlInputType {
    String value();
}
