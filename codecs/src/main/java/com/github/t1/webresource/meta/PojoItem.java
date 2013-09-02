package com.github.t1.webresource.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

import com.github.t1.stereotypes.Annotations;

public class PojoItem implements Item {
    protected final Object object;
    protected final Class<?> type;
    protected List<Trait> traits = null;
    private final AnnotatedElement annotations;

    public <T> PojoItem(Class<T> type, T object) {
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
    public List<Trait> traits() {
        if (traits == null) {
            this.traits = new PojoTraits(type);
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
