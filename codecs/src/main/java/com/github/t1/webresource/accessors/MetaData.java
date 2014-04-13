package com.github.t1.webresource.accessors;

/**
 * makes storing and retreiving meta data from a {@link MetaDataStore} a little bit typesafe.
 * 
 * @param <T>
 *            unused... only syntactic sugar.
 */
public interface MetaData<T> {
    public String title();
}
