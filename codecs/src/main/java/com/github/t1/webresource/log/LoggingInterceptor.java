package com.github.t1.webresource.log;

import static java.lang.Character.*;

import javax.interceptor.*;

import org.slf4j.*;

import com.github.t1.stereotypes.Annotations;
import com.google.common.annotations.VisibleForTesting;

@Logged
@Interceptor
public class LoggingInterceptor {

    @AroundInvoke
    Object aroundInvoke(InvocationContext context) throws Exception {
        Logged loggedAnnotation = Annotations.on(context.getMethod()).getAnnotation(Logged.class);
        Class<?> loggerType = loggedAnnotation.logger();
        Logger log = getLogger((loggerType != null) ? loggerType : context.getTarget().getClass());
        LogLevel logLevel = loggedAnnotation.level();

        if (logLevel.isEnabled(log))
            logLevel.log(log, message(context), context.getParameters());
        try {
            Object result = context.proceed();
            if (context.getMethod().getReturnType() != void.class)
                logLevel.log(log, "returns {}", result);
            return result;
        } catch (Exception e) {
            logLevel.log(log, "failed: " + message(context), e);
            throw e;
        }
    }

    @VisibleForTesting
    Logger getLogger(Class<?> type) {
        return LoggerFactory.getLogger(type);
    }

    private String message(InvocationContext context) {
        StringBuilder out = new StringBuilder();
        Logged loggedAnnotation = Annotations.on(context.getMethod()).getAnnotation(Logged.class);
        if ("".equals(loggedAnnotation.value())) {
            camelToSpaces(context.getMethod().getName(), out);
        } else {
            out.append(loggedAnnotation.value());
        }
        return out.toString();
    }

    private void camelToSpaces(String string, StringBuilder out) {
        for (Character c : string.toCharArray()) {
            if (isUpperCase(c)) {
                out.append(' ');
                out.append(toLowerCase(c));
            } else {
                out.append(c);
            }
        }
    }
}
