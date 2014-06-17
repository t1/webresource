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
    public Iterator<Entry> iterator() {
        final Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
        return new Iterator<Entry>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Entry next() {
                Map.Entry<String, Object> next = iterator.next();
                return new Entry(new Primitive(next.getKey()), items.of(next.getValue()));
            }

            @Override
            public void remove() {
                iterator.remove();
            }
        };
    }

    @Override
    public Primitive keyTitle() {
        return new Primitive(accessor.keyTitle(map));
    }

    @Override
    public Primitive valueTitle() {
        return new Primitive(accessor.valueTitle(map));
    }
}
