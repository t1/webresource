package com.github.t1.webresource.log;

import static java.lang.Character.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
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
        private final RestorableMdc mdc = new RestorableMdc();

        public Logging(InvocationContext context) {
            this.context = context;
            this.loggedAnnotation = Annotations.on(context.getMethod()).getAnnotation(Logged.class);
            this.logLevel = loggedAnnotation.level();
            Class<?> loggerType = loggedAnnotation.logger();
            this.log = getLogger((loggerType == void.class) ? context.getTarget().getClass() : loggerType);
        }

        public void logCall() {
            addParamaterLogContexts();
            addLogContextVariables();

            if (logLevel.isEnabled(log)) {
                logLevel.log(log, message(), context.getParameters());
            }
        }

        private void addParamaterLogContexts() {
            Annotation[][] parameterAnnotations = method().getParameterAnnotations();
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

        private Method method() {
            return context.getMethod();
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

        private void addLogContextVariables() {
            for (LogContextVariable variable : variables) {
                if (variable == null) // producers are allowed to return null
                    continue;
                String key = variable.getKey();
                String value = variable.getValue();
                mdc.put(key, value);
            }
        }

        private String message() {
            if ("".equals(loggedAnnotation.value())) {
                return camelToSpaces(method().getName())
                        + messageParamPlaceholders(method().getParameterTypes().length);
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

        private String messageParamPlaceholders(int length) {
            StringBuilder out = new StringBuilder();
            for (int i = 0; i < length; i++)
                out.append(" {}");
            return out.toString();
        }

        public void logResult(Object result) {
            if (method().getReturnType() != void.class) {
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

    @Inject
    Instance<LogContextVariable> variables;

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
