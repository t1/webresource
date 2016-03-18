package com.github.t1.webresource.codec;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/** Adds additional css style sheets to the page, or to all pages in an annotated package. */
@Retention(RUNTIME)
@Target({ TYPE, PACKAGE, ANNOTATION_TYPE })
public @interface HtmlStyleSheet {
    /** The URI to load the style sheet from */
    String value();

    /**
     * Optional integrity checksum for safe CDN usage.
     *
     * @see "[Do not let your CDN betray you](https://hacks.mozilla.org/2015/09/subresource-integrity-in-firefox-43)"
     */
    String integrity() default "";
}
