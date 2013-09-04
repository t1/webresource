package com.github.t1.webresource.meta;

import java.lang.reflect.AnnotatedElement;
import java.util.*;

import com.github.t1.stereotypes.Annotations;

class ListItem extends AbstractPojoItem {

    public <T> ListItem(T object) {
        super(object);
    }

    @Override
    protected <T> AnnotatedElement annotations() {
        if (empty()) {
            return new NullAnnotatedElement();
        } else {
            return Annotations.on(elementType());
        }
    }

    private boolean empty() {
        return ((List<?>) object).isEmpty();
    }

    private Class<? extends Object> elementType() {
        return ((List<?>) object).get(0).getClass();
    }

    @Override
    public boolean isList() {
        return true;
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
            if (empty())
                return Collections.emptyList();
            this.traits = new PojoTraits(elementType());
        }
        return traits;
    }
}
