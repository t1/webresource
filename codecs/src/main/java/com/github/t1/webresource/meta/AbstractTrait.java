package com.github.t1.webresource.meta;

import java.lang.annotation.Annotation;

public abstract class AbstractTrait implements Trait {
    /** <code>package private</code>, as this is only for {@link AbstractItem#read(Trait)} */
    abstract Object read(Object object);

    /** <code>package private</code>, as this is only for {@link AbstractItem#write(Trait, Item)} */
    abstract void write(Object object, Object value);

    @Override
    public int compareTo(Trait that) {
        return this.name().compareTo(that.name());
    }

    @Override
    public <T extends Annotation> boolean is(Class<T> annotation) {
        return false;
    }

    @Override
    public <T extends Annotation> T get(Class<T> annotation) {
        return null;
    }
}
