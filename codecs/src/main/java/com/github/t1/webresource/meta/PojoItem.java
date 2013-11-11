package com.github.t1.webresource.meta;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.github.t1.stereotypes.Annotations;

class PojoItem extends AbstractItem {
    public PojoItem(Object object) {
        super(object);
    }

    @Override
    protected List<Trait> fetchAllTraits() {
        return traitCollector().run();
    }

    private AbstractPojoTraitCollector traitCollector() {
        if (Annotations.on(type).isAnnotationPresent(XmlRootElement.class)) {
            return new JaxbPojoTraitCollector(type);
        } else {
            return new DefaultPojoTraitCollector(type);
        }
    }

    @Override
    public String toString() {
        return object.toString();
    }
}
