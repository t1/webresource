package com.github.t1.webresource.log;

import static java.lang.Character.*;

import javax.interceptor.*;

import org.slf4j.*;

import com.github.t1.stereotypes.Annotations;
import com.google.common.annotations.VisibleForTesting;

@Logged
@Interceptor
public class LoggingInterceptor {

    private class LoggingContext {
        private final InvocationContext context;
        private final Logged loggedAnnotation;
        private final Logger log;
        private final LogLevel logLevel;

        public LoggingContext(InvocationContext context) {
            this.context = context;
            this.loggedAnnotation = Annotations.on(context.getMethod()).getAnnotation(Logged.class);
            Class<?> loggerType = loggedAnnotation.logger();
            this.log = getLogger((loggerType != null) ? loggerType : context.getTarget().getClass());
            this.logLevel = loggedAnnotation.level();
        }

        public void logCall() {
            if (logLevel.isEnabled(log)) {
                logLevel.log(log, message(), context.getParameters());
            }
        }

        private String message() {
            if ("".equals(loggedAnnotation.value())) {
                return camelToSpaces(context.getMethod().getName());
            } else {
                return loggedAnnotation.value();
            }
        }

        private String camelToSpaces(String string) {
            StringBuilder out = new StringBuilder();
            for (Character c : string.toCharArray()) {
                if (isUpperCase(c)) {
                    out.append(' ');
                    out.append(toLowerCase(c));
                } else {
                    out.append(c);
                }
            }
            return out.toString();
        }

        public void logResult(Object result) {
            if (context.getMethod().getReturnType() != void.class) {
                logLevel.log(log, "returns {}", result);
            }
        }

        public void logException(Exception e) {
            logLevel.log(log, "failed", e);
        }
    }

    @AroundInvoke
    Object aroundInvoke(InvocationContext context) throws Exception {
        LoggingContext logging = new LoggingContext(context);

        logging.logCall();

        try {
            Object result = context.proceed();
            logging.logResult(result);
            return result;
        } catch (Exception e) {
            logging.logException(e);
            throw e;
        }
    }

    @VisibleForTesting
    Logger getLogger(Class<?> type) {
        return LoggerFactory.getLogger(type);
    }
}
