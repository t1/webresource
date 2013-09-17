package com.github.t1.webresource.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

import javax.persistence.Id;

import com.github.t1.stereotypes.Annotations;
import com.google.common.collect.ImmutableMap;


abstract class AbstractPojoItem implements Item {

    protected final Object object;
    protected final Class<?> type;
    protected List<Trait> traits = null;
    protected Map<String, Trait> traitMap = null;
    protected final AnnotatedElement annotations;

    public AbstractPojoItem(Object object) {
        this.object = object;
        this.type = (object == null) ? null : object.getClass();
        this.annotations = annotations();
    }

    protected <T> AnnotatedElement annotations() {
        return Annotations.on(type);
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public boolean isSimple() {
        return false;
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
    public Item get(Trait trait) {
        Object value = ((AbstractTrait) trait).of(this.object);
        return Items.newItem(value);
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

    @Override
    public <A extends Annotation> Trait trait(Class<A> type) {
        for (Trait trait : traits()) {
            if (trait.is(type)) {
                return trait;
            }
        }
        return null;
    }

    @Override
    public String type() {
        return (object == null) ? null : object.getClass().getSimpleName().toLowerCase() + "s";
    }

    @Override
    public Trait id() {
        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                return new PojoFieldTrait(field);
            }
        }
        return null;
    }

    @Override
    public <A extends Annotation> boolean is(Class<A> type) {
        return (annotations == null) ? false : annotations.isAnnotationPresent(type);
    }

    @Override
    public <A extends Annotation> A get(Class<A> type) {
        return (annotations == null) ? null : annotations.getAnnotation(type);
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