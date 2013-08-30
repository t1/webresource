package com.github.t1.webresource;

import static javax.xml.bind.annotation.XmlAccessType.*;

import javax.xml.bind.annotation.*;

public class PojoPropertiesJaxbStrategy extends PojoPropertiesAbstractStrategy {
    public PojoPropertiesJaxbStrategy(Class<?> type, PojoProperties target) {
        super(type, target);
    }

    @Override
    protected boolean pass(PojoFieldProperty field) {
        return !xmlTransient(field) && isVisible(FIELD, field);
    }

    @Override
    protected boolean pass(PojoGetterProperty getter) {
        return getter.isGetter() && !xmlTransient(getter) && isVisible(PROPERTY, getter);
    }

    private boolean xmlTransient(PojoProperty getter) {
        return getter.is(XmlTransient.class);
    }

    private boolean isVisible(XmlAccessType accessType, PojoProperty property) {
        return isAccessorVisible(accessType) || isPublicVisible(property) || isXmlVisible(property);
    }

    private boolean isAccessorVisible(XmlAccessType type) {
        return typeIs(XmlAccessorType.class) && typeAnnotation(XmlAccessorType.class).value() == type;
    }

    private boolean isPublicVisible(PojoProperty property) {
        return property.isPublicMember() && isAccessorVisible(PUBLIC_MEMBER);
    }

    private boolean isXmlVisible(Property property) {
        return property.is(XmlAttribute.class) || property.is(XmlElement.class);
    }

    @Override
    protected void init(PojoProperty property) {
        if (property.is(XmlElement.class)) {
            setName(property, property.get(XmlElement.class).name());
        } else if (property.is(XmlAttribute.class)) {
            setName(property, property.get(XmlAttribute.class).name());
        }
    }

    private void setName(PojoProperty property, String name) {
        if (!invalid(name)) {
            property.setName(name);
        }
    }

    private boolean invalid(String name) {
        return null == name || name.isEmpty() || "##default".equals(name);
    }
}
