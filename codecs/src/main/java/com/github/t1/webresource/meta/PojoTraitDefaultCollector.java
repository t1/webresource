package com.github.t1.webresource.meta;

import java.lang.reflect.AnnotatedElement;


public class PojoTraitDefaultCollector extends PojoTraitAbstractCollector {
    public PojoTraitDefaultCollector(Class<?> type, PojoTraits target, AnnotatedElement annotations) {
        super(type, target, annotations);
    }

    @Override
    protected boolean pass(PojoFieldTrait field) {
        return field.isPublicMember();
    }

    @Override
    protected boolean pass(PojoGetterTrait getter) {
        return getter.isPublicMember() && getter.isGetter();
    }
}
