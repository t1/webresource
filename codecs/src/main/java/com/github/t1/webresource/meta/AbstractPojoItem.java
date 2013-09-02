package com.github.t1.webresource.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.*;

import com.github.t1.stereotypes.Annotations;
import com.google.common.collect.ImmutableMap;


abstract class AbstractPojoItem implements Item {

    protected final Object object;
    protected final Class<?> type;
    protected List<Trait> traits = null;
    protected Map<String, Trait> traitMap = null;
    protected final AnnotatedElement annotations;

    public <T> AbstractPojoItem(Class<T> type, T object) {
        this.type = type;
        this.object = object;
        this.annotations = annotations();
    }

    protected <T> AnnotatedElement annotations() {
        return Annotations.on(type);
    }

    @Override
    public Object target() {
        return object;
    }

    @Override
    public boolean isList() {
        return false;
    }

    @Override
    public List<Item> getList() {
        throw new UnsupportedOperationException();
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
        if (traitMap == null)
            traitMap = buildTraitMap();
        Trait trait = traitMap.get(traitName);
        if (trait == null)
            throw new IllegalArgumentException("no trait " + traitName + " in " + type);
        return trait;
    }

    private Map<String, Trait> buildTraitMap() {
        ImmutableMap.Builder<String, Trait> map = ImmutableMap.builder();
        for (Trait trait : traits()) {
            map.put(trait.getName(), trait);
        }
        return map.build();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + type.getName() + "]";
    }

}