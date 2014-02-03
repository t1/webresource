package com.github.t1.webresource.typewriter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

public class MethodBuilder extends AnnotatableBuilder {

    final Class<Response> returnType;
    final String name;
    final String visibility = "public";
    final List<ParameterBuilder> parameters = new ArrayList<>();
    final StringWriter body = new StringWriter();

    public MethodBuilder(Class<Response> returnType, String methodName) {
        this.returnType = returnType;
        this.name = methodName;
    }

    public ParameterBuilder parameter(Class<?> type, String name) {
        ParameterBuilder builder = new ParameterBuilder(type, name);
        parameters.add(builder);
        return builder;
    }

    public PrintWriter body() {
        return new PrintWriter(body);
    }
}
