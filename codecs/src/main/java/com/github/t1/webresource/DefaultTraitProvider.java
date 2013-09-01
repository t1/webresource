package com.github.t1.webresource;


public class DefaultTraitProvider extends AbstractTraitProvider {
    public DefaultTraitProvider(Class<?> type, PojoTraits target) {
        super(type, target);
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
