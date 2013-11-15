package com.github.t1.webresource.log;

public interface LogContextConverter<T> {
    String convert(T object);
}
