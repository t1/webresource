package com.github.t1.webresource;

import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;

/**
 * Adds a CSS file to the header
 */
@Retention(RUNTIME)
public @interface HtmlStyleSheet {
    public String value();
}
