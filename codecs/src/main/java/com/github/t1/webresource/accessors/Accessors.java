package com.github.t1.webresource.accessors;

import java.net.URI;
import java.util.*;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.slf4j.*;

import com.google.common.annotations.VisibleForTesting;

public class Accessors {
    private final Logger log = LoggerFactory.getLogger(Accessors.class);

    @Inject
    Instance<Accessor<?>> instances;

    private Map<Class<?>, Accessor<?>> accessors;

    public <T> Accessor<T> of(T element) {
        Accessor<?> accessor = findAccessor(element.getClass());
        if (accessor == null)
            accessor = new Accessor<T>() {
                @Override
                public String title(Object element) {
                    return (element == null) ? null : element.toString();
                }

                @Override
                public URI link(Object element) {
                    return null;
                }
            };
        log.trace("found accessor for {}: {}", element, accessor);

        @SuppressWarnings("unchecked")
        Accessor<T> accessorT = (Accessor<T>) accessor;
        return accessorT;
    }

    private Accessor<?> findAccessor(Class<?> type) {
        Accessor<?> accessor = accessors().get(type);
        if (accessor == null) {
            for (Class<?> interface_ : type.getInterfaces()) {
                accessor = findAccessor(interface_);
            }
        }
        if (accessor == null) {
            Class<?> superclass = type.getSuperclass();
            if (superclass != null) {
                accessor = findAccessor(superclass);
            }
        }
        return accessor;
    }

    @VisibleForTesting
    Map<Class<?>, Accessor<?>> accessors() {
        if (accessors == null) {
            accessors = new HashMap<>();
            initAccessors();
        }
        return accessors;
    }

    private Map<Class<?>, Accessor<?>> initAccessors() {
        log.info("init accessors");
        for (Accessor<?> accessor : instances) {
            Class<?> type = new AccessorInfo(accessor).type();
            log.info("init accessor for {}: {}", type, accessor);
            if (type != null) {
                accessors.put(type, accessor);
            }
        }
        return accessors();
    }
}
