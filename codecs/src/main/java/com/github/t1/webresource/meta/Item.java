package com.github.t1.webresource.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.*;

import com.github.t1.stereotypes.Annotations;

/**
 * POJO meta object that provides data as {@link Trait}s and meta data as {@link Annotation}s, i.e. you don't have to
 * work with fields, getters, setters, etc. but work on a meta level. {@link #isSimple() Simple types} (incl.
 * <code>null</code>) are represented as items with one trait. Maps are represented just like objects (note that for
 * pojos and maps, the order of the traits is generally <i>not</i> guaranteed). {@link #isList() List} elements can be
 * accessed with {@link #getList()} that returns a list of Items.
 * <p/>
 * Design Decision: This class is currently based on reflection on POJOs, but the API is quite generic, so it should be
 * easy to extract an interface and write implementations that are based on e.g., xml, json, csv, maps, or any other
 * data structure with some sort of meta data facility, internal or external to the data itself. It would be
 * consequential to use some abstraction for meta data instead of annotations, but that would add complexity without
 * adding a lot of utility: Annotations are convenient to represent other meta data as well in a typesafe way, e.g. by
 * using <code>javax.enterprise.util.AnnotationLiteral</code>.
 */
public class Item {
    @SuppressWarnings("unchecked")
    public static <T> Item of(T object) {
        return new Item((Class<T>) ((object == null) ? null : object.getClass()), object);
    }

    public static final Trait SIMPLE = new SimpleTrait();
    private static final List<Trait> SIMPLE_TRAITS = Collections.<Trait> singletonList(SIMPLE);

    private final Object object;
    private final Class<?> type;
    private List<Trait> traits = null;
    private final AnnotatedElement annotations;

    private <T> Item(Class<T> type, T object) {
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

    public Object target() {
        return object;
    }

    public boolean isList() {
        return isList(type);
    }

    public List<Item> getList() {
        List<Item> result = new ArrayList<>();
        for (Object element : ((List<?>) object)) {
            result.add(Item.of(element));
        }
        return result;
    }

    public List<Trait> traits() {
        if (traits == null) {
            if (isSimple()) {
                this.traits = SIMPLE_TRAITS;
            } else if (isMap(type)) {
                this.traits = mapTraits();
            } else if (isList(type)) {
                this.traits = new PojoTraits(type);
            } else {
                this.traits = new PojoTraits(type);
            }
        }
        return traits;
    }

    public boolean isSimple() {
        return isNull() || type == String.class || Number.class.isAssignableFrom(type) || type == Boolean.class
                || type.isPrimitive();
    }

    public boolean isNull() {
        return object == null;
    }

    private List<Trait> mapTraits() {
        List<Trait> traits = new ArrayList<>();
        @SuppressWarnings("unchecked")
        Map<String, ?> map = (Map<String, ?>) object;
        for (String key : map.keySet()) {
            traits.add(new MapTrait(key));
        }
        return traits;
    }

    public Object get(Trait trait) {
        return trait.of(this.object);
    }

    public <A extends Annotation> boolean is(Class<A> type) {
        return (annotations == null) ? false : annotations.isAnnotationPresent(type);
    }

    public <A extends Annotation> A get(Class<A> type) {
        return (annotations == null) ? null : annotations.getAnnotation(type);
    }

    public Trait trait(String traitName) {
        for (Trait trait : traits()) {
            if (traitName.equals(trait.getName())) {
                return trait;
            }
        }
        throw new IllegalArgumentException("no trait " + traitName + " in " + type);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + type.getName() + "]";
    }
}
