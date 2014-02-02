package com.github.t1.webresource.typewriter;

import java.lang.annotation.Annotation;
import java.util.*;

public class AnnotationBuilder implements Builder {
    final Class<? extends Annotation> type;
    final Map<String, Object> parameters = new HashMap<>();
    private final List<Class<?>> types = new ArrayList<>();

    public AnnotationBuilder(Class<? extends Annotation> type) {
        this.type = type;
        this.types.add(type);
    }

    public void value(String value) {
        parameters.put("value", value);
    }

    public void type(Enum<?> enumValue) {
        parameters.put("type", enumValue);
    }

    @Override
    public List<Class<?>> types() {
        return types;
    }
}
