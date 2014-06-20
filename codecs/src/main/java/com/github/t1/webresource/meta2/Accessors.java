package com.github.t1.webresource.meta2;

import java.net.URI;
import java.util.*;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Accessors {
    private final Map<Class<?>, Accessor<?>> accessors = new HashMap<>();

    @Inject
    public Accessors(Instance<Accessor<?>> instances) {
        log.trace("init accessors");
        for (Accessor<?> accessor : instances) {
            Class<?> type = new AccessorInfo(accessor).type();
            log.trace("init accessor for {}: {}", type, accessor);
            if (type != null) {
                accessors.put(type, accessor);
            }
        }
    }

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

    private Map<Class<?>, Accessor<?>> accessors() {
        return accessors;
    }
}
