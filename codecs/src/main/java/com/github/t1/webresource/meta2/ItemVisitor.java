package com.github.t1.webresource.meta2;

public interface ItemVisitor {
    void visit(Primitive<?> primitive);

    void visit(Sequence sequence);

    void visit(Compound compound);
}
