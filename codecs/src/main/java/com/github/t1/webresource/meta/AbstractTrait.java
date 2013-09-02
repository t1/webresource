package com.github.t1.webresource.meta;

import java.lang.annotation.Annotation;

public abstract class AbstractTrait implements Trait {

    @Override
    public int compareTo(Trait that) {
        return this.getName().compareTo(that.getName());
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
