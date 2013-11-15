package com.github.t1.webresource.meta;

import java.lang.reflect.AnnotatedElement;
import java.util.*;

import com.github.t1.stereotypes.Annotations;
import com.github.t1.webresource.WebResourceTypeInfo;

class ListItem extends AbstractItem {

    public ListItem(Object list) {
        super(list);
    }

    private Class<?> elementType() {
        if (empty())
            return null;
        Object firstElement = collection().iterator().next();
        return (firstElement instanceof Class) ? (Class<?>) firstElement : firstElement.getClass();
    }

    @Override
    public AnnotatedElement annotations() {
        if (empty()) {
            return new NullAnnotatedElement();
        } else {
            return Annotations.on(elementType());
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
        return new WebResourceTypeInfo(elementType().getSimpleName()).plural;
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
        Item elementItem = Items.newItem(collection().iterator().next());
        return elementItem.traits();
    }
}
