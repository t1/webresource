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
        Logger log = getLogger(context.getTarget().getClass());
        Logged loggedAnnotation = Annotations.on(context.getMethod()).getAnnotation(Logged.class);
        LogLevel logLevel = loggedAnnotation.level();

        if (logLevel.isEnabled(log))
            logLevel.log(log, message(context));
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
        camelToSpaces(context.getMethod().getName(), out);
        appendParameters(context.getParameters(), out);
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

    private void appendParameters(Object[] parameters, StringBuilder out) {
        for (Object parameter : parameters) {
            out.append(' ').append(parameter);
        }
    }
}
