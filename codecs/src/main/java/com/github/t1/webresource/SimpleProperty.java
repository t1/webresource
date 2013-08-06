package com.github.t1.webresource;

import java.lang.annotation.Annotation;
import java.util.Objects;

public class SimpleProperty implements Property {

    @Override
    public <T extends Annotation> boolean is(Class<T> type) {
        return false;
    }

    @Override
    public String of(Object object) {
        return Objects.toString(object, null);
    }

    @Override
    public boolean isTransient() {
        return false;
    }

    @Override
    public String getName() {
        return "value";
    }
}
