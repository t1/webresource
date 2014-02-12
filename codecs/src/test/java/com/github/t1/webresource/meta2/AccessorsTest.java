package com.github.t1.webresource.meta2;

import static java.util.Arrays.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.Serializable;
import java.net.URI;
import java.util.Iterator;

import javax.enterprise.inject.Instance;

import org.junit.Test;

public class AccessorsTest {
    private Accessors givenAccessors(@SuppressWarnings("rawtypes") Accessor... accessor) {
        Accessors accessors = new Accessors();
        accessors.instances = accessorInstances(accessor);
        accessors.init();
        return accessors;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Instance<Accessor<?>> accessorInstances(Accessor... accessors) {
        Instance<Accessor<?>> mock = mock(Instance.class);
        when(mock.iterator()).thenReturn((Iterator) asList(accessors).iterator());
        return mock;
    }

    @Test
    public void shouldFindDefaultAccessor() throws Exception {
        Accessors accessors = givenAccessors();

        Accessor<String> accessor = accessors.of("dummy");

        assertEquals("test", accessor.title("test"));
    }

    @Test
    public void shouldReturnNullTitleFromDefaultAccessor() throws Exception {
        Accessors accessors = givenAccessors();

        Accessor<String> accessor = accessors.of("dummy");

        assertNull(accessor.title(null));
    }

    @Test
    public void shouldSkipRawAccessor() throws Exception {
        @SuppressWarnings("rawtypes")
        Accessors accessors = givenAccessors(new Accessor() {
            @Override
            public String title(Object element) {
                return "[" + element + "]";
            }

            @Override
            public URI link(Object element) {
                return null;
            }
        });

        Accessor<String> accessor = accessors.of("dummy");

        assertEquals("test", accessor.title("test"));
    }

    private final class RunnableAccessor implements Serializable, Accessor<String> {
        private static final long serialVersionUID = 1L;

        @Override
        public String title(String element) {
            return "x" + element;
        }

        @Override
        public URI link(String element) {
            return null;
        }
    }

    @Test
    public void shouldSkipOtherInterfaceBeforeAccessor() throws Exception {
        Accessors accessors = givenAccessors(new RunnableAccessor());

        Accessor<String> accessor = accessors.of("dummy");

        assertEquals("xtest", accessor.title("test"));
    }

    @Test
    public void shouldFindIntegerAccessor() throws Exception {
        Accessors accessors = givenAccessors(new Accessor<Integer>() {
            @Override
            public String title(Integer element) {
                return "[" + element + "]";
            }

            @Override
            public URI link(Integer element) {
                return null;
            }
        });

        Accessor<Integer> accessor = accessors.of(123);

        assertEquals("[456]", accessor.title(456));
    }

    @Test
    public void shouldFindSuperclassAccessor() throws Exception {
        Accessors accessors = givenAccessors(new Accessor<Number>() {
            @Override
            public String title(Number element) {
                return "n:" + element;
            }

            @Override
            public URI link(Number element) {
                return null;
            }
        });

        Accessor<Integer> accessor = accessors.of(123);

        assertEquals("n:456", accessor.title(456));
    }

    @Test
    public void shouldFindInterfaceAccessor() throws Exception {
        Accessors accessors = givenAccessors(new Accessor<Runnable>() {
            @Override
            public String title(Runnable element) {
                return "r";
            }

            @Override
            public URI link(Runnable element) {
                return null;
            }
        });

        Runnable runnable = new Runnable() {
            @Override
            public void run() {}
        };

        Accessor<Runnable> accessor = accessors.of(runnable);

        assertEquals("r", accessor.title(runnable));
    }
}
