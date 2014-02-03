package com.github.t1.webresource.typewriter;

public class ParameterBuilder extends AnnotatableBuilder {

    final Class<?> type;
    final String name;

    public ParameterBuilder(Class<?> type, String name) {
        this.type = type;
        this.name = name;
    }
}
