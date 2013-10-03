package com.github.t1.webresource.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

import com.github.t1.stereotypes.Annotations;
import com.github.t1.webresource.WebResourceTypeInfo;
import com.google.common.collect.*;


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
        return (type == null) ? null : Annotations.on(type);
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
    public <A extends Annotation> List<Trait> trait(Class<A> type) {
        ImmutableList.Builder<Trait> list = ImmutableList.builder();
        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(type)) {
                field.setAccessible(true);
                list.add(new PojoFieldTrait(field));
            }
        }
        return list.build();
    }

    @Override
    public String type() {
        return (object == null) ? null : new WebResourceTypeInfo(object.getClass().getSimpleName()).plural;
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
            map.put(trait.name(), trait);
        }
        return map.build();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + type.getName() + "]";
    }
}