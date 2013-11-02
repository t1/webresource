package com.github.t1.webresource.codec;

import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;

/**
 * Define the title of an item; used for the item's page title and for links to the item. When applied to a trait, then
 * this trait will be used; or all traits delimited by single spaces, if more than one trait is annotated as
 * {@link HtmlTitle}. When applied to a class, then the {@link #value()} will be used, after variables in the string
 * (like <code>${firstName}</code>) are replaced with the corresponding trait.
 */
@Retention(RUNTIME)
public @interface HtmlTitle {
    String value() default "";
}
