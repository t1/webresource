package com.github.t1.webresource.meta;

import static javax.xml.bind.annotation.XmlAccessType.*;

import java.lang.reflect.*;
import java.util.*;

import javax.xml.bind.annotation.*;

import com.github.t1.stereotypes.Annotations;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

public class JaxbPojoTraitCollector extends AbstractPojoTraitCollector {
    public JaxbPojoTraitCollector(Class<?> type) {
        super(type);
    }

    @Override
    protected Predicate<PojoTrait> fieldVisiblePredicate() {
        return new Predicate<PojoTrait>() {
            @Override
            public boolean apply(PojoTrait field) {
                return !xmlTransient(field) && isVisible(FIELD, field);
            }
        };
    }

    @Override
    protected Predicate<PojoTrait> accessorVisiblePredicate() {
        return new Predicate<PojoTrait>() {
            @Override
            public boolean apply(PojoTrait accessor) {
                return !xmlTransient(accessor) && isVisible(PROPERTY, accessor);
            }
        };
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
        return isPublic(trait.member()) && isAccessorVisible(PUBLIC_MEMBER);
    }

    private boolean isXmlVisible(Trait trait) {
        return trait.is(XmlAttribute.class) || trait.is(XmlElement.class);
    }

    @Override
    protected String traitName(Method method) {
        String traitName = annotatedName(Annotations.on(method));
        if (traitName != null)
            return traitName;
        return super.traitName(method);
    }

    private String annotatedName(AnnotatedElement annotations) {
        if (annotations.isAnnotationPresent(XmlElementWrapper.class)
                && valid(annotations.getAnnotation(XmlElementWrapper.class).name())) {
            return annotations.getAnnotation(XmlElementWrapper.class).name();
        }
        if (annotations.isAnnotationPresent(XmlElement.class)
                && valid(annotations.getAnnotation(XmlElement.class).name())) {
            return annotations.getAnnotation(XmlElement.class).name();
        }
        if (annotations.isAnnotationPresent(XmlAttribute.class)
                && valid(annotations.getAnnotation(XmlAttribute.class).name())) {
            return annotations.getAnnotation(XmlAttribute.class).name();
        }
        return null;
    }

    private boolean valid(String name) {
        return null != name && !name.isEmpty() && !"##default".equals(name);
    }

    @Override
    protected String traitName(Field field) {
        String annotatedName = annotatedName(Annotations.on(field));
        if (annotatedName != null)
            return annotatedName;
        return super.traitName(field);
    }

    @Override
    protected void sort(List<Trait> target) {
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

    private void orderBy(String[] propOrder, List<Trait> traits) {
        Map<String, Trait> map = map(traits);
        traits.clear();
        for (String name : propOrder) {
            traits.add(map.remove(name));
        }
        addAlphabetically(traits, map); // invisible traits don't have to be in the propOrder
    }

    private Map<String, Trait> map(List<Trait> traits) {
        Map<String, Trait> map = Maps.newHashMap();
        for (Trait trait : traits) {
            map.put(trait.name(), trait);
        }
        return map;
    }

    private void addAlphabetically(List<Trait> traits, Map<String, Trait> map) {
        List<String> keys = new ArrayList<>(map.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            traits.add(map.get(key));
        }
    }

    private boolean isXmlAccessOrderAlphabetical() {
        XmlAccessorOrder accessorOrder = typeAnnotation(XmlAccessorOrder.class);
        return accessorOrder != null && accessorOrder.value() == XmlAccessOrder.ALPHABETICAL;
    }
}
