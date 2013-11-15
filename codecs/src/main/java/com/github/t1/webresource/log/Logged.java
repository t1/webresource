package com.github.t1.webresource.log;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;

import javax.interceptor.InterceptorBinding;

@InterceptorBinding
@Target({ METHOD, TYPE })
@Retention(RUNTIME)
public @interface Logged {
    // Ideas:
    // level (also: OFF to override the Logged annotation propagated from the class to the method)
    // message (to override a default camel-case-to-space-separated)
    // logger
    // markers
}
