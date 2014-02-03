package com.github.t1.webresource.typewriter;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class AnnotatableBuilder {

    protected final List<AnnotationBuilder> annotations = new ArrayList<>();

    public AnnotatableBuilder() {
        super();
    }

    public AnnotationBuilder annotate(Class<? extends Annotation> type) {
        AnnotationBuilder builder = new AnnotationBuilder(type);
        annotations.add(builder);
        return builder;
    }
}
