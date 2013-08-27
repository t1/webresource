package com.github.t1.webresource;

import java.lang.annotation.Annotation;

public class SimpleProperty implements Property {

    @Override
    public String getName() {
        return "value";
    }

    @Override
    public Object of(Object object) {
        return object;
    }

    @Override
    public boolean isTransient() {
        return false;
    }

    @Override
    public <T extends Annotation> boolean is(Class<T> type) {
        return false;
    }

    @Override
    public <T extends Annotation> T get(Class<T> annotation) {
        return null;
    }
}
