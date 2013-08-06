package com.github.t1.webresource;

import java.lang.annotation.Annotation;


public interface Property {

    public abstract <T extends Annotation> boolean is(Class<T> type);

    public abstract String of(Object object);

    public abstract boolean isTransient();

    public abstract String getName();

}