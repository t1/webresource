package com.github.t1.webresource.codec;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;

/**
 * A list of {@link HtmlStyleSheet}s
 */
@Retention(RUNTIME)
@Target({ TYPE, ANNOTATION_TYPE, PACKAGE })
@Inherited
public @interface HtmlStyleSheets {
    public HtmlStyleSheet[] value();
}
