package com.github.t1.webresource.codec;

import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;

/**
 * When applied to a field, then this field will be used as the body of links pointing here. When applied to a class,
 * then the {@link #value()} will be used, after variables in the string (like <code>${firstName}</code>) are replaced
 * with the corresponding field content.
 */
@Retention(RUNTIME)
public @interface HtmlLinkText {
    String value() default "";
}
