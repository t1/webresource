package com.github.t1.webresource.codec;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Puts the pojo into a [bootstrap basic panel](http://getbootstrap.com/components/#panels-basic).
 *
 * When put on a package, all types in the package get a panel.
 *
 * If you want the panel to have a `panel-heading`, annotate the type (or the package) additionally {@link HtmlTitle}.
 */
@Retention(RUNTIME)
@Target({ TYPE, PACKAGE, ANNOTATION_TYPE })
public @interface HtmlPanel {}
