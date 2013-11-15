package com.github.t1.webresource.log;

import static java.lang.Character.*;

import java.lang.annotation.Annotation;

import javax.interceptor.*;

import org.slf4j.*;

import com.github.t1.stereotypes.Annotations;
import com.google.common.annotations.VisibleForTesting;

@Logged
@Interceptor
public class LoggingInterceptor {
    private class Logging {
        private final InvocationContext context;
        private final Logged loggedAnnotation;
        private final LogLevel logLevel;
        private final Logger log;
        private RestorableMdc mdc;

        public Logging(InvocationContext context) {
            this.context = context;
            this.loggedAnnotation = Annotations.on(context.getMethod()).getAnnotation(Logged.class);
            this.logLevel = loggedAnnotation.level();
            Class<?> loggerType = loggedAnnotation.logger();
            this.log = getLogger((loggerType != null) ? loggerType : context.getTarget().getClass());
        }

        public void logCall() {
            this.mdc = new RestorableMdc();
            putMdc();
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

        private void putMdc() {
            Annotation[][] parameterAnnotations = context.getMethod().getParameterAnnotations();
            for (int i = 0; i < parameterAnnotations.length; i++) {
                Annotation[] annotations = parameterAnnotations[i];
                for (Annotation annotation : annotations) {
                    if (annotation instanceof LogContext) {
                        LogContext logContext = (LogContext) annotation;
                        String key = logContext.value();
                        String valueString = convert(logContext.converter(), context.getParameters()[i]);
                        mdc.put(key, valueString);
                    }
                }
            }
        }

        private String convert(Class<? extends LogContextConverter<?>> converterType, Object valueObject) {
            try {
                @SuppressWarnings("unchecked")
                LogContextConverter<Object> converter = (LogContextConverter<Object>) converterType.newInstance();
                return converter.convert(valueObject);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }

        public void logResult(Object result) {
            if (context.getMethod().getReturnType() != void.class) {
                logLevel.log(log, "returns {}", result);
            }
        }

        public void logException(Exception e) {
            logLevel.log(log, "failed", e);
        }

        public void done() {
            mdc.restore();
        }
    }

    @AroundInvoke
    Object aroundInvoke(InvocationContext context) throws Exception {
        Logging logging = new Logging(context);

        logging.logCall();

        try {
            Object result = context.proceed();
            logging.logResult(result);
            return result;
        } catch (Exception e) {
            logging.logException(e);
            throw e;
        } finally {
            logging.done();
        }
    }

    @VisibleForTesting
    Logger getLogger(Class<?> type) {
        return LoggerFactory.getLogger(type);
    }
}
