package com.github.t1.webresource.log;

import static org.mockito.Mockito.*;

import java.lang.reflect.Method;

import javax.interceptor.InvocationContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

@RunWith(MockitoJUnitRunner.class)
public class LoggingInterceptorTest {
    @InjectMocks
    LoggingInterceptor interceptor = new LoggingInterceptor() {
        @Override
        Logger getLogger(java.lang.Class<?> type) {
            return logger;
        };
    };
    @Mock
    InvocationContext context;
    @Mock
    Logger logger;

    private void whenMethod(Method method, Object... args) throws ReflectiveOperationException {
        when(context.getTarget()).thenReturn("dummy");
        when(context.getMethod()).thenReturn(method);
        when(context.getParameters()).thenReturn(args);
    }

    @Test
    public void shouldLogALongMethodNameWithSpaces() throws Exception {
        whenMethod(LoggingInterceptorTest.class.getMethod("shouldLogALongMethodNameWithSpaces"));

        interceptor.aroundInvoke(context);

        verify(logger).debug("should log a long method name with spaces");
    }

    @Test
    public void shouldLogReturnValue() throws Exception {
        whenMethod(String.class.getMethod("isEmpty"));
        when(context.proceed()).thenReturn(true);

        interceptor.aroundInvoke(context);

        verify(logger).debug("returns {}", true);
    }

    @Test
    public void shouldNotLogVoidReturnValue() throws Exception {
        whenMethod(LoggingInterceptorTest.class.getMethod("shouldNotLogVoidReturnValue"));
        when(context.proceed()).thenReturn(true);

        interceptor.aroundInvoke(context);

        verify(logger).debug("should not log void return value");
        verifyNoMoreInteractions(logger);
    }

    @Test
    public void shouldLogOneParameter() throws Exception {
        whenMethod(String.class.getMethod("codePointAt", int.class), 3);

        interceptor.aroundInvoke(context);

        verify(logger).debug("code point at 3");
    }

    @Test
    public void shouldLogFourParameters() throws Exception {
        whenMethod(String.class.getMethod("regionMatches", int.class, String.class, int.class, int.class), //
                3, "other", 5, 7);

        interceptor.aroundInvoke(context);

        verify(logger).debug("region matches 3 other 5 7");
    }
}
