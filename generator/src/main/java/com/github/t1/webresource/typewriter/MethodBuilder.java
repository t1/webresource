package com.github.t1.webresource.typewriter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class MethodBuilder extends AnnotatableBuilder {

    final TypeString returnType;
    final String name;
    String visibility = "public";
    final List<ParameterBuilder> parameters = new ArrayList<>();
    final StringWriter body = new StringWriter();

    public MethodBuilder(Class<?> returnType, String methodName) {
        this(new TypeString(returnType), methodName);
    }

    public MethodBuilder(TypeString returnType, String methodName) {
        this.returnType = returnType;
        this.name = methodName;
    }

    public ParameterBuilder parameter(Class<?> type, String name) {
        ParameterBuilder builder = new ParameterBuilder(type, name);
        parameters.add(builder);
        return builder;
    }

    public ParameterBuilder parameter(TypeString type, String name) {
        ParameterBuilder builder = new ParameterBuilder(type, name);
        parameters.add(builder);
        return builder;
    }

    public PrintWriter body() {
        return new PrintWriter(body);
    }

    public MethodBuilder private_() {
        this.visibility = "private";
        return this;
    }
}
