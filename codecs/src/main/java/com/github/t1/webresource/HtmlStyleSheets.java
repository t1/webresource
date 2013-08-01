package com.github.t1.webresource;

import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;

/**
 * A list of {@link HtmlStyleSheet}s
 */
@Retention(RUNTIME)
public @interface HtmlStyleSheets {
    public HtmlStyleSheet[] value();
}
