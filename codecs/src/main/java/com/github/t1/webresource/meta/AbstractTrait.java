package com.github.t1.webresource.meta;

import java.lang.annotation.Annotation;

public abstract class AbstractTrait implements Trait {
    /** <code>package private</code>, as this is only for {@link AbstractPojoItem#get(Trait)} */
    abstract Object of(Object object);

    /** <code>package private</code>, as this is only for {@link AbstractPojoItem#set(Trait, Item)} */
    abstract void set(Object object, Object value);

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
