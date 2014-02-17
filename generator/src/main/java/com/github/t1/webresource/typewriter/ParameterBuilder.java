package com.github.t1.webresource.typewriter;

public class ParameterBuilder extends AnnotatableBuilder {

    final TypeString type;
    final String name;
    String uncollectedType;

    public ParameterBuilder(Class<?> type, String name) {
        this(new TypeString(type), name);
    }

    public ParameterBuilder(TypeString type, String name) {
        this.type = type;
        this.name = name;
    }

    public void generic(String uncollectedType) {
        this.uncollectedType = uncollectedType;
    }
}
