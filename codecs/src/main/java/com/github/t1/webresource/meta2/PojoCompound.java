package com.github.t1.webresource.meta2;

import java.lang.reflect.Field;
import java.util.*;

import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent = true)
public class PojoCompound implements Compound {
    private final Object pojo;
    private final Items items;

    private final List<Property> properties = new ArrayList<>();

    public PojoCompound(Object pojo, Items items) {
        this.pojo = pojo;
        this.items = items;
        initProperties();
    }

    private void initProperties() {
        for (Field field : pojo.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Primitive<String> fieldName = new Primitive<>(field.getName());
            properties.add(new Property(fieldName, value(field)));
        }
    }

    private Item value(Field field) {
        try {
            return items.of(field.get(pojo));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Primitive<String> keyTitle() {
        return null;
    }

    @Override
    public Primitive<String> valueTitle() {
        return null;
    }

    @Override
    public void visit(ItemVisitor visitor) {
        visitor.visit(this);
    }
}
