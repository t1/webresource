package com.github.t1.webresource;

import javax.xml.bind.annotation.*;

public class PojoPropertiesJaxbStrategy extends PojoPropertiesAbstractStrategy {
    public PojoPropertiesJaxbStrategy(Class<?> type, PojoProperties target) {
        super(type, target);
    }

    @Override
    protected boolean pass(PojoFieldProperty field) {
        return isXmlVisible(field);
    }

    @Override
    protected boolean pass(PojoGetterProperty getter) {
        if (isXmlVisible(getter)) {
            if (getter.is(XmlElement.class))
                getter.setName(getter.get(XmlElement.class).name());
            else if (getter.is(XmlAttribute.class))
                getter.setName(getter.get(XmlAttribute.class).name());
            return true;
        }
        return false;
    }

    private boolean isXmlVisible(Property property) {
        return property.is(XmlAttribute.class) || property.is(XmlElement.class);
    }
}
