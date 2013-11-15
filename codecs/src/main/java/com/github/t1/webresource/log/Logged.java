package com.github.t1.webresource.log;

import static com.github.t1.webresource.log.LogLevel.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;

import javax.interceptor.InterceptorBinding;

@InterceptorBinding
@Target({ METHOD, TYPE })
@Retention(RUNTIME)
public @interface Logged {
    public LogLevel level() default DEBUG;
    // Ideas:
    // message (to override a default camel-case-to-space-separated)
    // logger (other than the method's class)
    // markers (from string array)
}
