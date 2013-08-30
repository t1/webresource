package com.github.t1.webresource;


public class PojoPropertiesDefaultStrategy extends PojoPropertiesAbstractStrategy {
    public PojoPropertiesDefaultStrategy(Class<?> type, PojoProperties target) {
        super(type, target);
    }

    @Override
    protected boolean pass(PojoFieldProperty field) {
        return field.isPublicMember();
    }

    @Override
    protected boolean pass(PojoGetterProperty getter) {
        return getter.isPublicMember() && getter.isGetter();
    }
}
