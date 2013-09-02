package com.github.t1.webresource.meta;

import java.lang.annotation.Annotation;

public interface Trait extends Comparable<Trait> {
    public abstract Object of(Object object);

    public abstract String getName();

    public abstract <T extends Annotation> boolean is(Class<T> annotation);

    public abstract <T extends Annotation> T get(Class<T> annotation);
}
