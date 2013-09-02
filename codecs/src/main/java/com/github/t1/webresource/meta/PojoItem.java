package com.github.t1.webresource.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.*;

import com.github.t1.stereotypes.Annotations;

public class PojoItem implements Item {
    protected final Object object;
    private final Class<?> type;
    protected List<Trait> traits = null;
    private final AnnotatedElement annotations;

    public <T> PojoItem(Class<T> type, T object) {
        this.type = type;
        this.object = object;
        this.annotations = annotations();
    }

    private static <T> boolean isList(Class<T> type) {
        return List.class.isAssignableFrom(type);
    }

    protected <T> AnnotatedElement annotations() {
        if (type == null)
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

    @Override
    public Object target() {
        return object;
    }

    @Override
    public boolean isList() {
        return isList(type);
    }

    @Override
    public List<Item> getList() {
        List<Item> result = new ArrayList<>();
        for (Object element : ((List<?>) object)) {
            result.add(Items.newItem(element));
        }
        return result;
    }

    @Override
    public List<Trait> traits() {
        if (traits == null) {
            if (isList(type)) {
                this.traits = new PojoTraits(type);
            } else {
                this.traits = new PojoTraits(type);
            }
        }
        return traits;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public Object get(Trait trait) {
        return trait.of(this.object);
    }

    @Override
    public <A extends Annotation> boolean is(Class<A> type) {
        return (annotations == null) ? false : annotations.isAnnotationPresent(type);
    }

    @Override
    public <A extends Annotation> A get(Class<A> type) {
        return (annotations == null) ? null : annotations.getAnnotation(type);
    }

    @Override
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
