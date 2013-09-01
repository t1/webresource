package com.github.t1.webresource;

import static javax.xml.bind.annotation.XmlAccessType.*;

import javax.xml.bind.annotation.*;

public class JaxbTraitProvider extends AbstractTraitProvider {
    public JaxbTraitProvider(Class<?> type, PojoTraits target) {
        super(type, target);
    }

    @Override
    protected boolean pass(PojoFieldTrait field) {
        return !xmlTransient(field) && isVisible(FIELD, field);
    }

    @Override
    protected boolean pass(PojoGetterTrait getter) {
        return getter.isGetter() && !xmlTransient(getter) && isVisible(PROPERTY, getter);
    }

    private boolean xmlTransient(PojoTrait getter) {
        return getter.is(XmlTransient.class);
    }

    private boolean isVisible(XmlAccessType accessType, PojoTrait trait) {
        return isAccessorVisible(accessType) || isPublicVisible(trait) || isXmlVisible(trait);
    }

    private boolean isAccessorVisible(XmlAccessType type) {
        return typeIs(XmlAccessorType.class) && typeAnnotation(XmlAccessorType.class).value() == type;
    }

    private boolean isPublicVisible(PojoTrait trait) {
        return trait.isPublicMember() && isAccessorVisible(PUBLIC_MEMBER);
    }

    private boolean isXmlVisible(Trait trait) {
        return trait.is(XmlAttribute.class) || trait.is(XmlElement.class);
    }

    @Override
    protected void init(PojoTrait trait) {
        if (trait.is(XmlElement.class)) {
            setName(trait, trait.get(XmlElement.class).name());
        } else if (trait.is(XmlAttribute.class)) {
            setName(trait, trait.get(XmlAttribute.class).name());
        }
    }

    private void setName(PojoTrait trait, String name) {
        if (!invalid(name)) {
            trait.setName(name);
        }
    }

    private boolean invalid(String name) {
        return null == name || name.isEmpty() || "##default".equals(name);
    }
}
