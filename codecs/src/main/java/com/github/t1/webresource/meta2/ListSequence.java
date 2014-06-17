package com.github.t1.webresource.meta2;

import java.util.*;

import lombok.Value;

@Value
class ListSequence implements Sequence {
    List<?> list;
    Items items;

    @Override
    public void visit(ItemVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Iterator<Item> iterator() {
        final Iterator<?> iterator = list.iterator();
        return new Iterator<Item>() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Item next() {
                return items.of(iterator.next());
            }

            @Override
            public void remove() {
                iterator.remove();
            }
        };
    }
}