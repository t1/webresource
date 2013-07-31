package com.github.t1.webresource;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;

/**
 * Make the {@link HtmlWriter} render this field additionally in the <code>html/head</code> element. If there is more
 * than one field annotated as {@link HtmlHead}, all are included in the head, separated by " - ".
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface HtmlHead {}
