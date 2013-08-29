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
        // @XmlAccessorType(NONE)
        return isXmlVisible(getter);
    }

    private boolean isXmlVisible(Property property) {
        return property.is(XmlAttribute.class) || property.is(XmlElement.class);
    }

    @Override
    protected void init(PojoProperty property) {
        if (property.is(XmlElement.class)) {
            property.setName(property.get(XmlElement.class).name());
        } else if (property.is(XmlAttribute.class)) {
            property.setName(property.get(XmlAttribute.class).name());
        }
    }
}
