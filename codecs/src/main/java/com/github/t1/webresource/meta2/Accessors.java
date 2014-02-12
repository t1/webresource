package com.github.t1.webresource.meta2;

import java.lang.reflect.*;
import java.net.URI;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.slf4j.*;

public class Accessors {
    private final Logger log = LoggerFactory.getLogger(Accessors.class);

    @Inject
    Instance<Accessor<?>> instances;

    private final Map<Class<?>, Accessor<?>> accessors = new HashMap<>();

    @PostConstruct
    void init() {
        log.info("init accessors");
        for (Accessor<?> accessor : instances) {
            Class<?> type = type(accessor);
            log.info("init accessor for {}: {}", type, accessor);
            if (type != null) {
                accessors.put(type, accessor);
            }
        }
    }

    private Class<?> type(Accessor<?> accessor) {
        for (Type type : accessor.getClass().getGenericInterfaces()) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                if (Accessor.class.equals(parameterizedType.getRawType())) {
                    return (Class<?>) parameterizedType.getActualTypeArguments()[0];
                }
            }
            if (isRawTypeAccessor(type)) {
                return null;
            }
        }
        throw new IllegalArgumentException("this can't happen");
    }

    private boolean isRawTypeAccessor(Type type) {
        return Accessor.class.equals(type);
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
        log.info("found accessor for {}: {}", element, accessor);

        @SuppressWarnings("unchecked")
        Accessor<T> accessorT = (Accessor<T>) accessor;
        return accessorT;
    }

    private Accessor<?> findAccessor(Class<?> type) {
        Accessor<?> accessor = accessors.get(type);
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
}
