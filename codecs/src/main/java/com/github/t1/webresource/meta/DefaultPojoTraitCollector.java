package com.github.t1.webresource.meta;

import com.github.t1.webresource.meta.PojoTrait.Predicate;

public class DefaultPojoTraitCollector extends AbstractPojoTraitCollector {
    public DefaultPojoTraitCollector(Class<?> type) {
        super(type);
    }

    @Override
    protected Predicate<PojoTrait> fieldVisiblePredicate() {
        return new Predicate<PojoTrait>() {
            @Override
            public boolean apply(PojoTrait input) {
                return isPublic(input.member());
            }
        };
    }

    @Override
    protected Predicate<PojoTrait> accessorVisiblePredicate() {
        return new Predicate<PojoTrait>() {
            @Override
            public boolean apply(PojoTrait input) {
                return isPublic(input.member());
            }
        };
    }
}
