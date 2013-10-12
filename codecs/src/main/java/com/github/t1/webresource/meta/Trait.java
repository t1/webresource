package com.github.t1.webresource.meta;

import java.lang.annotation.Annotation;

/** A property of an {@link Item}. */
public interface Trait extends Comparable<Trait> {
    public abstract String name();

    public abstract String type();

    public abstract boolean visible();

    public abstract <T extends Annotation> boolean is(Class<T> annotation);

    public abstract <T extends Annotation> T get(Class<T> annotation);
}
