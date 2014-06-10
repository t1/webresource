package com.github.t1.webresource.accessors;

import static java.util.Arrays.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.Serializable;
import java.net.URI;
import java.util.*;

import javax.enterprise.inject.Instance;

import org.junit.Test;

public class AccessorsTest {
    private Accessors givenAccessors(@SuppressWarnings("rawtypes") Accessor... accessors) {
        Accessors result = new Accessors();
        result.instances = accessorInstances(accessors);
        return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Instance<Accessor<?>> accessorInstances(Accessor... accessors) {
        Instance<Accessor<?>> mock = mock(Instance.class);
        when(mock.iterator()).thenReturn((Iterator) asList(accessors).iterator());
        return mock;
    }

    @Test
    public void shouldFindDefaultAccessor() {
        Accessors accessors = givenAccessors();

        Accessor<String> accessor = accessors.of("dummy");

        assertEquals("test", accessor.title("test"));
    }

    @Test
    public void shouldReturnNullTitleFromDefaultAccessor() {
        Accessors accessors = givenAccessors();

        Accessor<String> accessor = accessors.of("dummy");

        assertNull(accessor.title(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWithRawAccessor() {
        @SuppressWarnings("rawtypes")
        Accessor rawAccessor = new Accessor() {
            @Override
            public String title(Object element) {
                return "[" + element + "]";
            }

            @Override
            public URI link(Object element) {
                return null;
            }
        };

        Accessors accessors = givenAccessors(rawAccessor);

        accessors.of("dummy");
    }

    @Test
    public void shouldFindPrimitiveAccessor() {
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
    public void shouldFindSuperclassAccessor() {
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
    public void shouldFindInterfaceAccessor() {
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

    private static class AbstractAccessorImpl extends AbstractAccessor<URI> {
        @Override
        public URI link(URI element) {
            return element;
        }
    }

    @Test
    public void shouldFindAbstractAccessorImpl() {
        Accessors accessors = givenAccessors(new AbstractAccessorImpl());

        URI uri = URI.create("http://localhost");
        Accessor<URI> accessor = accessors.of(uri);

        assertEquals(uri, accessor.link(uri));
    }

    private static class SuperAbstractAccessorImpl extends AbstractAccessorImpl {}

    @Test
    public void shouldFindSuperAbstractAccessorImpl() {
        Accessors accessors = givenAccessors(new SuperAbstractAccessorImpl());

        URI uri = URI.create("http://localhost");
        Accessor<URI> accessor = accessors.of(uri);

        assertEquals(uri, accessor.link(uri));
    }

    private final class OtherInterfaceBeforeAccessor implements Serializable, Accessor<String> {
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
    public void shouldSkipOtherInterfaceBeforeAccessor() {
        Accessors accessors = givenAccessors(new OtherInterfaceBeforeAccessor());

        Accessor<String> accessor = accessors.of("dummy");

        assertEquals("xtest", accessor.title("test"));
    }

    public class GenericTypeAccessor extends AbstractAccessor<List<?>> {
        @Override
        public URI link(List<?> element) {
            return null;
        }
    }

    @Test
    public void shouldFindGenericTypeAccessor() {
        Accessors accessors = givenAccessors(new GenericTypeAccessor());

        List<String> list = Arrays.asList("one", "two", "three");
        Accessor<List<String>> accessor = accessors.of(list);

        assertEquals(GenericTypeAccessor.class, accessor.getClass());
    }
}
