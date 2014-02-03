package com.github.t1.webresource.typewriter;

import java.util.ArrayList;
import java.util.List;

public class ClassBuilder extends AnnotatableBuilder {
    final String pkg;
    final String type;
    final List<FieldBuilder> fields = new ArrayList<>();
    final List<MethodBuilder> methods = new ArrayList<>();

    public ClassBuilder(String pkg, String type) {
        this.pkg = pkg;
        this.type = type;
    }

    public FieldBuilder field(Class<?> type, String string) {
        FieldBuilder builder = new FieldBuilder(type, string);
        fields.add(builder);
        return builder;
    }

    public MethodBuilder method(Class<?> returnType, String methodName) {
        MethodBuilder builder = new MethodBuilder(returnType, methodName);
        methods.add(builder);
        return builder;
    }
}
