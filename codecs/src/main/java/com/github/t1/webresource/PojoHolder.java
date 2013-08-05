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
 * abstraction for meta data instead of annotations, but that would add an additional level of abstraction without
 * adding a lot of utility.
 */
public class PojoHolder {

    private final Object object;
    private List<PojoProperty> properties = null;
    private final AnnotatedElement annotations;

    public PojoHolder(Object object) {
        this.object = object;
        this.annotations = (object == null) ? null : Annotations.on(object.getClass());
    }

    public List<PojoProperty> properties() {
        if (properties == null) {
            properties = new ArrayList<>();
            for (Field field : object.getClass().getDeclaredFields()) {
                PojoProperty property = new PojoProperty(object, field);
                if (property.isTransient())
                    continue;
                properties.add(property);
            }
        }
        return properties;
    }

    public <T extends Annotation> boolean is(Class<T> type) {
        return annotations.isAnnotationPresent(type);
    }

    public <T extends Annotation> T get(Class<T> type) {
        return annotations.getAnnotation(type);
    }

    public boolean isList() {
        return object instanceof List;
    }
}
