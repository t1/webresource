package com.github.t1.webresource.codec2;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.List;

import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

public class HtmlPartResover {
    public HtmlPartWriter of(Object item) {
        HtmlPartWriter resolved = resolve(item);
        inject(resolved);
        return resolved;
    }

    private HtmlPartWriter resolve(Object item) {
        if (item instanceof List) {
            return new HtmlListPartWriter((List<?>) item);
        } else if (item instanceof URI) {
            return new HtmlLinkPartWriter((URI) item);
        } else {
            return new ToStringPartWriter(item);
        }
    }

    private void inject(Object target) {
        Class<? extends Object> type = target.getClass();
        for (Field field : type.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                field.setAccessible(true);
                try {
                    field.set(target, instance(field.getType()));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private Object instance(Class<?> type) {
        return CDI.current().select(type).get(); // TODO this is not for real
    }
}
