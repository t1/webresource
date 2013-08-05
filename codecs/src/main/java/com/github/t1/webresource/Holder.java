package com.github.t1.webresource;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

import com.github.t1.stereotypes.Annotations;

/**
 * Holds a pojo and provides data and meta data to it by using reflection, i.e. you don't have to work with fields,
 * getters, setters, etc. but use properties and features.
 * <p/>
 * Design Decision: This class tries to be quite generic, i.e. it should be easy to extract an interface and write
 * implementations that are not based on reflection, but, e.g., xml, json, csv, maps, or any other data structure with
 * some sort of meta data faciliy, internal or external to the data itself. Then it would be nice to use some
 * abstraction for meta data instead of annotations, but that would add complexity without adding a lot of utility.
 */
public class Holder {

    private static final List<Property> SIMPLE_PROPERTIES = Collections.singletonList(Property.SIMPLE);

    private final Object object;
    private List<Property> properties = null;
    private final AnnotatedElement annotations;

    public Holder(Object object) {
        this.object = object;
        this.annotations = (object == null) ? null : Annotations.on(object.getClass());
    }

    public boolean isNull() {
        return object == null;
    }

    public boolean isSimple() {
        return isNull() || object instanceof String || object instanceof Number || object instanceof Boolean
                || object.getClass().isPrimitive();
    }

    public boolean isList() {
        return object instanceof List;
    }

    public List<Holder> getList() {
        List<Holder> result = new ArrayList<>();
        for (Object element : ((List<?>) object)) {
            result.add(new Holder(element));
        }
        return result;
    }

    public List<Property> properties() {
        if (properties == null) {
            if (isSimple()) {
                properties = SIMPLE_PROPERTIES;
            } else {
                properties = new ArrayList<>();
                for (Field field : object.getClass().getDeclaredFields()) {
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

    public <T extends Annotation> boolean is(Class<T> type) {
        return annotations.isAnnotationPresent(type);
    }

    public <T extends Annotation> T get(Class<T> type) {
        return annotations.getAnnotation(type);
    }

    public Property property(String propertyName) {
        for (Property property : properties()) {
            if (propertyName.equals(property.getName())) {
                return property;
            }
        }
        throw new IllegalArgumentException("no property " + propertyName + " in " + object.getClass());
    }
}
