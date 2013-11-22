package com.github.t1.webresource.log;

import static com.github.t1.webresource.log.LogLevel.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.util.Collections;

import javax.enterprise.inject.Instance;
import javax.interceptor.InvocationContext;

import lombok.experimental.Value;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.slf4j.*;

@RunWith(MockitoJUnitRunner.class)
public class LoggingInterceptorTest {
    private final class StoreMdcAnswer implements Answer<Void> {
        private final String kEY;
        private final String[] userId;

        private StoreMdcAnswer(String kEY, String[] userId) {
            this.kEY = kEY;
            this.userId = userId;
        }

        @Override
        public Void answer(InvocationOnMock invocation) throws Throwable {
            userId[0] = MDC.get(kEY);
            return null;
        }
    }

    @InjectMocks
    LoggingInterceptor interceptor = new LoggingInterceptor() {
        @Override
        Logger getLogger(java.lang.Class<?> type) {
            LoggingInterceptorTest.this.loggerType = type;
            return logger;
        };
    };
    @Mock
    InvocationContext context;
    @Mock
    Logger logger;
    @Mock
    Instance<LogContextVariable> variables;

    Class<?> loggerType;

    @Before
    public void setup() {
        when(logger.isDebugEnabled()).thenReturn(true);
        when(variables.iterator()).thenReturn(Collections.<LogContextVariable> emptyList().iterator());
    }

    private void whenMethod(Object target, String methodName, Object... args) throws ReflectiveOperationException {
        Method method = target.getClass().getMethod(methodName, types(args));
        whenMethod(method, target, args);
    }

    private void whenMethod(Method method, Object target, Object... args) {
        when(context.getMethod()).thenReturn(method);
        when(context.getTarget()).thenReturn(target);
        when(context.getParameters()).thenReturn(args);
    }

    private Class<?>[] types(Object[] objects) {
        Class<?>[] result = new Class[objects.length];
        for (int i = 0; i < objects.length; i++) {
            result[i] = objects[i].getClass();
        }
        return result;
    }

    @Test
    public void shouldLogALongMethodNameWithSpaces() throws Exception {
        class Container {
            @Logged
            public void methodWithALongName() {}
        }
        whenMethod(new Container(), "methodWithALongName");

        interceptor.aroundInvoke(context);

        verify(logger).debug("method with a long name", new Object[0]);
    }

    @Test
    public void shouldLogAnAnnotatedMethod() throws Exception {
        class Container {
            @Logged("bar")
            public void foo() {}
        }
        whenMethod(new Container(), "foo");

        interceptor.aroundInvoke(context);

        verify(logger).debug("bar", new Object[0]);
    }

    @Test
    public void shouldLogReturnValue() throws Exception {
        class Container {
            @Logged
            public boolean methodWithReturnType() {
                return true;
            }
        }
        whenMethod(new Container(), "methodWithReturnType");
        when(context.proceed()).thenReturn(true);

        interceptor.aroundInvoke(context);

        verify(logger).debug("returns {}", new Object[] { true });
    }

    @Test
    public void shouldLogException() throws Exception {
        class Container {
            @Logged
            public boolean methodThatMightFail() {
                return true;
            }
        }
        whenMethod(new Container(), "methodThatMightFail");
        RuntimeException exception = new RuntimeException("foo");
        when(context.proceed()).thenThrow(exception);

        try {
            interceptor.aroundInvoke(context);
            fail("RuntimeException expected");
        } catch (RuntimeException e) {
            // that's okay
        }
        verify(logger).debug("failed", exception);
    }

    @Test
    public void shouldNotLogVoidReturnValue() throws Exception {
        class Container {
            @Logged
            public void voidReturnType() {}
        }
        whenMethod(new Container(), "voidReturnType");

        interceptor.aroundInvoke(context);

        verify(logger).debug("void return type", new Object[0]);
        verify(logger, atLeast(0)).isDebugEnabled();
        verifyNoMoreInteractions(logger);
    }

    @Test
    public void shouldLogIntParameter() throws Exception {
        class Container {
            @Logged
            public void methodWithIntArgument(int i) {}
        }
        Method method = Container.class.getMethod("methodWithIntArgument", int.class);
        whenMethod(method, new Container(), 3);

        interceptor.aroundInvoke(context);

        verify(logger).debug("method with int argument {}", new Object[] { 3 });
    }

    @Test
    public void shouldLogIntegerParameter() throws Exception {
        class Container {
            @Logged
            public void methodWithIntegerArgument(Integer i) {}
        }
        whenMethod(new Container(), "methodWithIntegerArgument", 3);

        interceptor.aroundInvoke(context);

        verify(logger).debug("method with integer argument {}", new Object[] { 3 });
    }

    @Test
    public void shouldLogTwoParameters() throws Exception {
        class Container {
            @Logged
            public void methodWithTwoParameters(String one, String two) {}
        }
        whenMethod(new Container(), "methodWithTwoParameters", "foo", "bar");

        interceptor.aroundInvoke(context);

        verify(logger).debug("method with two parameters {} {}", new Object[] { "foo", "bar" });
    }

    @Test
    public void shouldNotLogWhenOff() throws Exception {
        class Container {
            @Logged(level = OFF)
            public void atOff() {}
        }
        whenMethod(new Container(), "atOff");

        interceptor.aroundInvoke(context);

        verifyNoMoreInteractions(logger);
    }

    @Test
    public void shouldNotLogWhenDebugIsNotEnabled() throws Exception {
        class Container {
            @Logged(level = DEBUG)
            public void atDebug() {}
        }
        when(logger.isDebugEnabled()).thenReturn(false);
        whenMethod(new Container(), "atDebug");

        interceptor.aroundInvoke(context);

        verify(logger, atLeast(0)).isDebugEnabled();
        verifyNoMoreInteractions(logger);
    }

    @Test
    public void shouldLogInfoWhenInfoIsEnabled() throws Exception {
        class Container {
            @Logged(level = INFO)
            public void atInfo() {}
        }
        when(logger.isInfoEnabled()).thenReturn(true);
        whenMethod(new Container(), "atInfo");

        interceptor.aroundInvoke(context);

        verify(logger).info("at info", new Object[0]);
    }

    @Test
    public void shouldUseExplicitLoggerClass() throws Exception {
        class Container {
            @Logged(logger = Integer.class)
            public void foo() {}
        }
        whenMethod(new Container(), "foo");

        interceptor.aroundInvoke(context);

        assertEquals(Integer.class, loggerType);
    }

    @Test
    public void shouldDefaultToContainerLoggerClass() throws Exception {
        class Container {
            @Logged
            public void foo() {}
        }
        whenMethod(new Container(), "foo");

        interceptor.aroundInvoke(context);

        assertEquals(Container.class, loggerType);
    }

    @Test
    public void shouldLogContextParameter() throws Exception {
        final String KEY = "user-id";
        class Container {
            @Logged
            public void methodWithLogContextParameter(@LogContext(KEY) String one, String two) {}
        }
        whenMethod(new Container(), "methodWithLogContextParameter", "foo", "bar");
        final String[] userId = new String[1];
        when(context.proceed()).thenAnswer(new StoreMdcAnswer(KEY, userId));

        MDC.put(KEY, "bar");
        interceptor.aroundInvoke(context);
        assertEquals("bar", MDC.get(KEY));

        verify(logger).debug("method with log context parameter {} {}", new Object[] { "foo", "bar" });
        assertEquals("foo", userId[0]);
    }

    @Value
    static class Pojo {
        String one, two;
    }

    static class PojoConverter implements LogContextConverter<Pojo> {
        @Override
        public String convert(Pojo object) {
            return object.one;
        }
    }

    @Test
    public void shouldConvertLogContextParameter() throws Exception {
        final String KEY = "foobar";
        class Container {
            @Logged
            public void foo(@LogContext(value = KEY, converter = PojoConverter.class) Pojo pojo) {}
        }
        whenMethod(new Container(), "foo", new Pojo("a", "b"));
        final String[] oneValue = new String[1];
        when(context.proceed()).thenAnswer(new StoreMdcAnswer(KEY, oneValue));

        MDC.put(KEY, "bar");
        interceptor.aroundInvoke(context);
        assertEquals("bar", MDC.get(KEY));

        verify(logger).debug("foo {}", new Object[] { new Pojo("a", "b") });
        assertEquals("a", oneValue[0]);
    }

    @Test
    public void shouldFindAddALogContextVariable() throws Exception {
        class Container {
            @Logged
            public void foo() {}
        }
        whenMethod(new Container(), "foo");
        final String[] version = new String[1];
        when(context.proceed()).thenAnswer(new StoreMdcAnswer("version", version));

        LogContextVariable variable = new LogContextVariable("version", "1.0");
        when(variables.iterator()).thenReturn(Collections.<LogContextVariable> singletonList(variable).iterator());

        MDC.put("version", "bar");
        interceptor.aroundInvoke(context);
        assertEquals("bar", MDC.get("version"));

        verify(logger).debug("foo", new Object[0]);
        assertEquals("1.0", version[0]);
    }

    @Test
    public void shouldSkipNullLogContextVariable() throws Exception {
        class Container {
            @Logged
            public void foo() {}
        }
        whenMethod(new Container(), "foo");
        final String[] version = new String[1];
        when(context.proceed()).thenAnswer(new StoreMdcAnswer("version", version));

        when(variables.iterator()).thenReturn(Collections.<LogContextVariable> singletonList(null).iterator());

        MDC.put("version", "bar");
        interceptor.aroundInvoke(context);
        assertEquals("bar", MDC.get("version"));

        verify(logger).debug("foo", new Object[0]);
        assertEquals("bar", version[0]);
    }
}
