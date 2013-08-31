package com.github.t1.webresource;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.*;

import com.github.t1.stereotypes.Annotations;

/**
 * Holds a pojo and provides data and meta data to it by using reflection, i.e. you don't have to work with fields,
 * getters, setters, etc. but use properties and meta properties. {@link #isSimple() Simple types} (incl.
 * <code>null</code>) are represented as objects with one property. Maps are represented just like objects (note that
 * for pojos and maps, the order of the properties is generally <i>not</i> guaranteed). {@link #isList() List} elements
 * can be accessed with {@link #getList()}.
 * <p/>
 * Design Decision: This class tries to be quite generic, so it should be easy to extract an interface and write
 * implementations that are not based on reflection on pojos, but, e.g., xml, json, csv, maps, or any other data
 * structure, ideally with some sort of meta data facility, internal or external to the data itself. Then it would be
 * consequential to use some abstraction for meta data instead of annotations, but that would add complexity without
 * adding a lot of utility: Annotations are convenient to represent other meta data as well in a typesafe way, e.g. by
 * using <code>javax.enterprise.util.AnnotationLiteral</code>.
 */
public class Holder<T> {

    public static final Property SIMPLE = new SimpleProperty();
    private static final List<Property> SIMPLE_PROPERTIES = Collections.<Property> singletonList(SIMPLE);

    private final T object;
    private final Class<T> type;
    private List<Property> properties = null;
    private final AnnotatedElement annotations;

    @SuppressWarnings("unchecked")
    public Holder(T object) {
        this((Class<T>) ((object == null) ? null : object.getClass()), object);
    }

    public Holder(Class<T> type, T object) {
        this.type = type;
        this.object = object;
        this.annotations = annotations(type, object);
    }

    private static <T> boolean isList(Class<T> type) {
        return List.class.isAssignableFrom(type);
    }

    private static <T> AnnotatedElement annotations(Class<T> type, T object) {
        if (type == null)
            return null;
        if (isMap(type))
            return null;
        if (isList(type)) {
            if (((List<?>) object).isEmpty()) {
                return new NullAnnotatedElement();
            } else {
                return Annotations.on(((List<?>) object).get(0).getClass());
            }
        }
        return Annotations.on(type);
    }

    private static boolean isMap(Class<?> type) {
        return Map.class.isAssignableFrom(type);
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
        return isList(type);
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
                this.properties = SIMPLE_PROPERTIES;
            } else if (isMap(type)) {
                this.properties = mapProperties();
            } else {
                this.properties = new PojoProperties(type);
            }
        }
        return properties;
    }

    private List<Property> mapProperties() {
        List<Property> properties = new ArrayList<>();
        @SuppressWarnings("unchecked")
        Map<String, ?> map = (Map<String, ?>) object;
        for (String key : map.keySet()) {
            properties.add(new MapProperty(key));
        }
        return properties;
    }

    public Object get(Property property) {
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
