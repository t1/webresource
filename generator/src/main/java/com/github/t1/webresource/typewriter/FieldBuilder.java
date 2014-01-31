package com.github.t1.webresource.typewriter;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public class FieldBuilder implements Builder {

    private final Class<?> type;
    private final String name;
    private boolean final_;
    private final String visibility = "private";
    private String initialization;

    public FieldBuilder(Class<?> type, String name) {
        this.type = type;
        this.name = name;
    }

    public FieldBuilder final_() {
        this.final_ = true;
        return this;
    }

    public FieldBuilder init(String expression) {
        this.initialization = " = " + expression;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append(visibility).append(" ");
        if (final_)
            out.append("final ");
        out.append(type.getSimpleName()).append(" ").append(name);
        out.append(initialization);
        out.append(";");
        return out.toString();
    }

    @Override
    public Set<Class<?>> types() {
        return ImmutableSet.<Class<?>> of(type);
    }
}
