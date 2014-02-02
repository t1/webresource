package com.github.t1.webresource.typewriter;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class FieldBuilder implements Builder {
    final Class<?> type;
    final String name;
    boolean final_;
    final String visibility = "private";
    String initialization = "";
    final List<Class<?>> types = new ArrayList<>();
    final List<AnnotationBuilder> annotations = new ArrayList<>();

    public FieldBuilder(Class<?> type, String name) {
        this.type = type;
        this.name = name;
        this.types.add(type);
    }

    public FieldBuilder final_() {
        this.final_ = true;
        return this;
    }

    public FieldBuilder init(String expression) {
        this.initialization = " = " + expression;
        return this;
    }

    public FieldBuilder using(Class<?> type) {
        types.add(type);
        return this;
    }

    public AnnotationBuilder annotate(Class<? extends Annotation> type) {
        AnnotationBuilder builder = new AnnotationBuilder(type);
        annotations.add(builder);
        return builder;
    }

    @Override
    public List<Class<?>> types() {
        return types;
    }
}
