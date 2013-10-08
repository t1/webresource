package com.github.t1.webresource.meta;

import static javax.xml.bind.annotation.XmlAccessType.*;

import java.lang.reflect.AnnotatedElement;
import java.util.*;

import javax.xml.bind.annotation.*;

public class PojoTraitJaxbCollector extends PojoTraitAbstractCollector {
    public PojoTraitJaxbCollector(Class<?> type, PojoTraits target, AnnotatedElement annotations) {
        super(type, target, annotations);
    }

    @Override
    protected boolean pass(PojoFieldTrait field) {
        return !xmlTransient(field) && isVisible(FIELD, field);
    }

    @Override
    protected boolean pass(PojoAccessorTrait getter) {
        return getter.isGetter() && !xmlTransient(getter) && isVisible(PROPERTY, getter);
    }

    private boolean xmlTransient(PojoTrait getter) {
        return getter.is(XmlTransient.class);
    }

    private boolean isVisible(XmlAccessType accessType, PojoTrait trait) {
        return isAccessorVisible(accessType) || isPublicVisible(trait) || isXmlVisible(trait);
    }

    private boolean isAccessorVisible(XmlAccessType type) {
        if (typeIs(XmlAccessorType.class)) {
            return typeAnnotation(XmlAccessorType.class).value() == type;
        } else {
            return type == PUBLIC_MEMBER; // this is the default
        }
    }

    private boolean isPublicVisible(PojoTrait trait) {
        return trait.isPublicMember() && isAccessorVisible(PUBLIC_MEMBER);
    }

    private boolean isXmlVisible(Trait trait) {
        return trait.is(XmlAttribute.class) || trait.is(XmlElement.class);
    }

    @Override
    protected void init(PojoTrait trait) {
        if (trait.is(XmlElementWrapper.class)) {
            setName(trait, trait.get(XmlElementWrapper.class).name());
        } else if (trait.is(XmlElement.class)) {
            setName(trait, trait.get(XmlElement.class).name());
        } else if (trait.is(XmlAttribute.class)) {
            setName(trait, trait.get(XmlAttribute.class).name());
        }
    }

    private void setName(PojoTrait trait, String name) {
        if (!invalid(name)) {
            trait.name(name);
        }
    }

    private boolean invalid(String name) {
        return null == name || name.isEmpty() || "##default".equals(name);
    }

    @Override
    protected void sort(PojoTraits target) {
        String[] propOrder = propOrder();
        if (propOrder != null) {
            orderBy(propOrder, target);
        } else if (isXmlAccessOrderAlphabetical()) {
            Collections.sort(target);
        }
    }

    private String[] propOrder() {
        XmlType xmlType = typeAnnotation(XmlType.class);
        if (xmlType != null) {
            String[] propOrder = xmlType.propOrder();
            if (!isEmpty(propOrder)) {
                return propOrder;
            }
        }
        return null;
    }

    private boolean isEmpty(String[] propOrder) {
        return propOrder == null || propOrder.length == 0 || (propOrder.length == 1 && propOrder[0].isEmpty());
    }

    private void orderBy(String[] propOrder, PojoTraits target) {
        Map<String, Trait> map = target.map();
        target.clear();
        for (String name : propOrder) {
            target.add(map.get(name));
        }
    }

    private boolean isXmlAccessOrderAlphabetical() {
        XmlAccessorOrder accessorOrder = typeAnnotation(XmlAccessorOrder.class);
        return accessorOrder != null && accessorOrder.value() == XmlAccessOrder.ALPHABETICAL;
    }
}
