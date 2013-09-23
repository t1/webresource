package com.github.t1.webresource.codec;

import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;

/**
 * The name of the label for input fields. Defaults to the name of the object property.
 */
@Retention(RUNTIME)
public @interface HtmlFieldName {
    String value();
}
