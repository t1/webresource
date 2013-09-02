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
        if (((List<?>) object).isEmpty()) {
            return new NullAnnotatedElement();
        } else {
            return Annotations.on(((List<?>) object).get(0).getClass());
        }
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
            this.traits = new PojoTraits(type);
        }
        return traits;
    }
}
