package com.github.t1.webresource.meta;

import java.lang.reflect.AnnotatedElement;
import java.util.*;

import com.github.t1.stereotypes.Annotations;
import com.github.t1.webresource.WebResourceTypeInfo;

class ListItem extends AbstractItem {

    public <T> ListItem(T object) {
        super(object);
    }

    @Override
    protected <T> AnnotatedElement annotations() {
        if (empty()) {
            return new NullAnnotatedElement();
        } else {
            return Annotations.on(element().getClass());
        }
    }

    private boolean empty() {
        return collection().isEmpty();
    }

    private Collection<?> collection() {
        return (Collection<?>) object;
    }

    @Override
    public String type() {
        return new WebResourceTypeInfo(element().getClass().getSimpleName()).plural;
    }

    private Object element() {
        return collection().iterator().next();
    }

    @Override
    public boolean isList() {
        return true;
    }

    @Override
    public List<Item> list() {
        List<Item> result = new ArrayList<>();
        for (Object element : collection()) {
            result.add(Items.newItem(element));
        }
        return result;
    }

    @Override
    protected Collection<Trait> fetchAllTraits() {
        if (empty())
            return Collections.emptyList();
        Item elementItem = Items.newItem(element());
        return elementItem.traits();
    }
}
