package com.github.t1.webresource;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

import com.github.t1.stereotypes.Annotations;

/**
 * Holds a pojo and provides data and meta data to it by using reflection, i.e. you don't have to work with fields,
 * getters, setters, etc. but use properties and meta properties.
 * <p/>
 * Design Decision: This class tries to be quite generic, i.e. it should be easy to extract an interface and write
 * implementations that are not based on reflection, but, e.g., xml, json, csv, maps, or any other data structure with
 * some sort of meta data faciliy, internal or external to the data itself. Then it would be nice to use some
 * abstraction for meta data instead of annotations, but that would add complexity without adding a lot of utility.
 */
public class Holder<T> {

    private static final List<Property> SIMPLE_PROPERTIES = Collections.singletonList(Property.SIMPLE);

    private final T object;
    private final Class<T> type;
    private List<Property> properties = null;
    private final AnnotatedElement annotations;

    public Holder(Class<T> type, T object) {
        this.type = type;
        this.object = object;
        this.annotations = (object == null) ? null : Annotations.on(type);
    }

    @SuppressWarnings("unchecked")
    public Holder(T object) {
        this((Class<T>) ((object == null) ? null : object.getClass()), object);
    }

    public T target() {
        return object;
    }

    public boolean isNull() {
        return object == null;
    }

    public boolean isSimple() {
        return isNull() || type == String.class || Number.class.isAssignableFrom(type) || type == Boolean.class
                || type.isPrimitive();
    }

    public boolean isList() {
        return object instanceof List;
    }

    public List<Holder<?>> getList() {
        List<Holder<?>> result = new ArrayList<>();
        for (Object element : ((List<?>) object)) {
            result.add(new Holder<>(element));
        }
        return result;
    }

    public List<Property> properties() {
        if (properties == null) {
            if (isSimple()) {
                properties = SIMPLE_PROPERTIES;
            } else {
                properties = new ArrayList<>();
                for (Field field : type.getDeclaredFields()) {
                    Property property = new Property(field);
                    if (property.isTransient())
                        continue;
                    properties.add(property);
                }
            }
        }
        return properties;
    }

    public String get(Property property) {
        return property.of(this.object);
    }

    public <A extends Annotation> boolean is(Class<A> type) {
        return (annotations == null) ? false : annotations.isAnnotationPresent(type);
    }

    public <A extends Annotation> A get(Class<A> type) {
        return (annotations == null) ? null : annotations.getAnnotation(type);
    }

    public Property property(String propertyName) {
        for (Property property : properties()) {
            if (propertyName.equals(property.getName())) {
                return property;
            }
        }
        throw new IllegalArgumentException("no property " + propertyName + " in " + type);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + type.getName() + "]";
    }
}
