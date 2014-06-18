package com.github.t1.webresource.meta2;

import java.util.*;

import lombok.Value;

@Value
public class MapCompound implements Compound {
    Map<String, Object> map;
    MapAccessor accessor;
    Items items;

    @Override
    public void visit(ItemVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Iterable<Property> properties() {
        return new Iterable<Property>() {
            @Override
            public Iterator<Property> iterator() {
                final Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
                return new Iterator<Property>() {
                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public Property next() {
                        Map.Entry<String, Object> next = iterator.next();
                        return new Property(new Primitive<>(next.getKey()), items.of(next.getValue()));
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    @Override
    public Primitive<String> keyTitle() {
        return new Primitive<>(accessor.keyTitle(map));
    }

    @Override
    public Primitive<String> valueTitle() {
        return new Primitive<>(accessor.valueTitle(map));
    }
}
