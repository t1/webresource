package com.github.t1.webresource.meta2;

import java.util.*;

import lombok.Value;
import lombok.experimental.Accessors;

import com.github.t1.webresource.meta2.PojoType.PojoProperty;

@Value
@Accessors(fluent = true)
public class PojoCompound implements Compound {
    private final PojoType type;
    private final Object pojo;
    private final Items items;

    private final List<Property> properties;

    public PojoCompound(Object pojo, Items items) {
        this.type = PojoType.of(pojo.getClass());
        this.pojo = pojo;
        this.items = items;
        this.properties = initProperties();
    }

    private List<Property> initProperties() {
        List<Property> list = new ArrayList<>();
        for (PojoProperty pojoProperty : type.pojoProperties()) {
            Primitive<String> name = new Primitive<>(pojoProperty.name());
            Item value = items.of(pojoProperty.get(pojo));
            list.add(new Property(name, value));
        }
        return list;
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
