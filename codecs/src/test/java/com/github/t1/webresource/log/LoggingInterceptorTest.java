package com.github.t1.webresource.log;

import static com.github.t1.webresource.log.LogLevel.*;
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

    private void whenDebugEnabled() {
        when(logger.isDebugEnabled()).thenReturn(true);
    }

    private void whenMethod(Method method, Object... args) throws ReflectiveOperationException {
        when(context.getTarget()).thenReturn("dummy");
        when(context.getMethod()).thenReturn(method);
        when(context.getParameters()).thenReturn(args);
    }

    @Test
    public void shouldLogALongMethodNameWithSpaces() throws Exception {
        class Container {
            @Logged
            public void methodWithALongName() {}
        }
        whenDebugEnabled();
        whenMethod(Container.class.getMethod("methodWithALongName"));

        interceptor.aroundInvoke(context);

        verify(logger).debug("method with a long name");
    }

    @Test
    public void shouldLogReturnValue() throws Exception {
        class Container {
            @Logged
            public boolean methodWithReturnType() {
                return true;
            }
        }
        whenDebugEnabled();
        whenMethod(Container.class.getMethod("methodWithReturnType"));
        when(context.proceed()).thenReturn(true);

        interceptor.aroundInvoke(context);

        verify(logger).debug("returns {}", new Object[] { true });
    }

    @Test
    public void shouldNotLogVoidReturnValue() throws Exception {
        class Container {
            @Logged
            public void voidReturnType() {}
        }
        whenDebugEnabled();
        whenMethod(Container.class.getMethod("voidReturnType"));
        when(context.proceed()).thenReturn(true);

        interceptor.aroundInvoke(context);

        verify(logger).debug("void return type");
        verify(logger, atLeast(0)).isDebugEnabled();
        verifyNoMoreInteractions(logger);
    }

    @Test
    public void shouldLogIntParameter() throws Exception {
        class Container {
            @Logged
            public void methodWithIntArgument(int i) {}
        }
        whenDebugEnabled();
        whenMethod(Container.class.getMethod("methodWithIntArgument", int.class), 3);

        interceptor.aroundInvoke(context);

        verify(logger).debug("method with int argument 3");
    }

    @Test
    public void shouldLogIntegerParameter() throws Exception {
        class Container {
            @Logged
            public void methodWithIntegerArgument(Integer i) {}
        }
        whenDebugEnabled();
        whenMethod(Container.class.getMethod("methodWithIntegerArgument", Integer.class), 3);

        interceptor.aroundInvoke(context);

        verify(logger).debug("method with integer argument 3");
    }

    @Test
    public void shouldLogTwoParameters() throws Exception {
        class Container {
            @Logged
            public void methodWithTwoParameters(String one, String two) {}
        }
        whenDebugEnabled();
        Method method = Container.class.getMethod("methodWithTwoParameters", String.class, String.class);
        whenMethod(method, "foo", "bar");

        interceptor.aroundInvoke(context);

        verify(logger).debug("method with two parameters foo bar");
    }

    public static class WithLogLevels {
        @Logged(level = OFF)
        public void atOff() {}

        // @Logged(level=TRACE) public void atTrace() {}
        @Logged(level = DEBUG)
        public void atDebug() {}

        @Logged(level = INFO)
        public void atInfo() {}
    }

    @Test
    public void shouldNotLogWhenOff() throws Exception {
        whenDebugEnabled();
        whenMethod(WithLogLevels.class.getMethod("atOff"));

        interceptor.aroundInvoke(context);

        verifyNoMoreInteractions(logger);
    }

    @Test
    public void shouldNotLogWhenDebugIsNotEnabled() throws Exception {
        whenMethod(WithLogLevels.class.getMethod("atDebug"));

        interceptor.aroundInvoke(context);

        verify(logger, atLeast(0)).isDebugEnabled();
        verifyNoMoreInteractions(logger);
    }

    @Test
    public void shouldLogInfoWhenInfoIsEnabled() throws Exception {
        whenDebugEnabled();
        when(logger.isInfoEnabled()).thenReturn(true);
        whenMethod(WithLogLevels.class.getMethod("atInfo"));

        interceptor.aroundInvoke(context);

        verify(logger).info("at info");
    }
}
