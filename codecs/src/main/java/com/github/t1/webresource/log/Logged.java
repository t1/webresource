package com.github.t1.webresource.log;

import static com.github.t1.webresource.log.LogLevel.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;

import javax.interceptor.InterceptorBinding;

/**
 * Logs the method invocation (the name of the method and the parameter values) and the return value resp. exception
 * thrown.
 * <p>
 * Note that an interceptor is not called, when you call a method locally (not to mention calling a private method)
 * <p>
 * TODO find out and document how to call through the interceptor stack on self
 * <p>
 * TODO: MDC: Parameters (optionally converted by some class) and Scanners (Version)
 */
@InterceptorBinding
@Target({ METHOD, TYPE })
@Retention(RUNTIME)
public @interface Logged {
    public LogLevel level() default DEBUG;

    /**
     * The format of the message to log. Defaults to a camel-case-to-space-separated string of the method name with the
     * space separated arguments appended. If you do provide a format, make sure to include enough placeholders ("{}")
     * for the arguments.
     */
    public String value() default "";

    /**
     * The class used to create the logger. Defaults to the class containing the method being logged.
     */
    public Class<?> logger() default void.class;
}
