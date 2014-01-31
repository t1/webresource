package com.github.t1.webresource.typewriter;

import java.lang.annotation.Annotation;
import java.util.*;

import com.google.common.collect.ImmutableSet;

public class AnnotationBuilder implements Builder {
    private final Class<? extends Annotation> type;
    private final Map<String, Object> parameters = new HashMap<>();

    public AnnotationBuilder(Class<? extends Annotation> type) {
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("@").append(type.getSimpleName());
        boolean first = true;
        if (!parameters.isEmpty()) {
            out.append("(");
            for (String name : parameters.keySet()) {
                if (first)
                    first = false;
                else
                    out.append(", ");
                if (!onlyValueParameter())
                    out.append(name).append(" = ");
                Object value = parameters.get(name);
                if (!isPrimitive(value))
                    out.append("\"");
                out.append(value);
                if (!isPrimitive(value))
                    out.append("\"");
            }
            out.append(")");
        }
        return out.toString();
    }

    private boolean isPrimitive(Object value) {
        return value instanceof Integer || value instanceof Boolean;
    }

    private boolean onlyValueParameter() {
        return parameters.size() == 1 && "value".equals(parameters.keySet().iterator().next());
    }

    public void value(String value) {
        parameters.put("value", value);
    }

    @Override
    public Set<Class<?>> types() {
        return ImmutableSet.<Class<?>> of(type);
    }
}
