package com.github.t1.webresource.annotations;

import javax.persistence.Id;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * By default the {@link Id primary key} will be used for the REST boundary, but sometimes a different, business key
 * will be more suitable.
 */
@Target({ FIELD })
@Retention(RUNTIME)
public @interface WebResourceKey {}
