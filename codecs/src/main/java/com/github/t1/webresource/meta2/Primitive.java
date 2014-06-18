package com.github.t1.webresource.meta2;

import lombok.Value;

@Value
public class Primitive<T> implements Item {
    T object;

    @Override
    public String toString() {
        return (object == null) ? "" : object.toString();
    }

    @Override
    public void visit(ItemVisitor visitor) {
        visitor.visit(this);
    }
}
