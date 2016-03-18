package com.github.t1.webresource.codec;

import com.github.t1.webresource.util.StringTool;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Sets the title for the pojo for the `<head><title>` as well as the `h1` in a {@link HtmlPanel}.
 *
 * You can set it on:
 * class
 * : If the {@link #value()} or (for lists) {@link #plural()} is not empty, that is used instead of a
 * stringified version of the type name. For a {@link HtmlPanel}, a `panel-heading` is generated, even if the
 * `HtmlTitle` is empty.
 *
 * field
 * : Use this field for the page title and {@link HtmlPanel}, as well as the items in a collection.
 *
 * package
 * : As if put on all types in the package. Note that you generally won't set the {@link #value()} or {@link #plural()}
 * package wide, but just use it to enable the `panel-heading` for all {@link HtmlPanel}s.
 */
@Retention(RUNTIME)
@Target({ FIELD, TYPE, PACKAGE, ANNOTATION_TYPE })
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
