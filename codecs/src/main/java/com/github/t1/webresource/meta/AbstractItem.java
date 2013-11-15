package com.github.t1.webresource.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.*;

import com.github.t1.stereotypes.Annotations;
import com.github.t1.webresource.WebResourceTypeInfo;
import com.google.common.collect.*;

public abstract class AbstractItem implements Item {
    protected final Object object;
    protected final Class<?> type;
    protected Map<String, Trait> traitMap = null;
    protected final AnnotatedElement annotations;
    private Collection<Trait> visibleTraits;

    public AbstractItem(Object object) {
        this.object = object;
        this.type = (object == null) ? null : object.getClass();
        this.annotations = annotations();
    }

    @Override
    public AnnotatedElement annotations() {
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
    public boolean isType() {
        return false;
    }

    @Override
    public boolean isList() {
        return false;
    }

    @Override
    public List<Item> list() {
        throw new UnsupportedOperationException("a " + getClass().getSimpleName() + " is not a list item");
    }

    @Override
    public final Collection<Trait> traits() {
        if (visibleTraits == null) {
            ImmutableList.Builder<Trait> builder = ImmutableList.builder();
            for (Trait trait : allTraits()) {
                if (trait.visible()) {
                    builder.add(trait);
                }
            }
            visibleTraits = builder.build();
        }
        return visibleTraits;
    }

    private Collection<Trait> allTraits() {
        return traitMap().values();
    }

    private Map<String, Trait> traitMap() {
        if (traitMap == null) {
            ImmutableMap.Builder<String, Trait> map = ImmutableMap.builder();
            for (Trait trait : fetchAllTraits()) {
                map.put(trait.name(), trait);
            }
            traitMap = map.build();
        }
        return traitMap;
    }

    protected abstract Collection<Trait> fetchAllTraits();

    @Override
    public Item read(Trait trait) {
        Object value = ((AbstractTrait) trait).read(this.object);
        return Items.newItem(value);
    }

    @Override
    public void write(Trait trait, Item value) {
        ((AbstractTrait) trait).write(this.object, ((AbstractItem) value).object);
    }

    @Override
    public Trait trait(String traitName) {
        Trait trait = traitMap().get(traitName);
        if (trait == null)
            throw new IllegalArgumentException("no trait " + traitName + " in " + type);
        return trait;
    }

    @Override
    public <A extends Annotation> List<Trait> trait(Class<A> type) {
        // TODO use filter instead
        ImmutableList.Builder<Trait> list = ImmutableList.builder();
        for (Trait trait : allTraits()) {
            if (trait.is(type)) {
                list.add(trait);
            }
        }
        return list.build();
    }

    @Override
    public String type() {
        return (object == null) ? null : new WebResourceTypeInfo(type.getSimpleName()).plural;
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
    public String toString() {
        return getClass().getSimpleName() + "[" + type.getName() + "]";
    }
}
