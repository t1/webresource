package com.github.t1.webresource.typewriter;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class ClassBuilder {
    final String pkg;
    final String type;
    final List<AnnotationBuilder> annotations = new ArrayList<>();
    final List<FieldBuilder> fields = new ArrayList<>();

    public ClassBuilder(String pkg, String type) {
        this.pkg = pkg;
        this.type = type;
    }

    public AnnotationBuilder annotate(Class<? extends Annotation> type) {
        AnnotationBuilder builder = new AnnotationBuilder(type);
        annotations.add(builder);
        return builder;
    }

    public FieldBuilder field(Class<?> type, String string) {
        FieldBuilder builder = new FieldBuilder(type, string);
        fields.add(builder);
        return builder;
    }
}
