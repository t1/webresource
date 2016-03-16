package com.github.t1.webresource.codec;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.*;

@Retention(RUNTIME)
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
