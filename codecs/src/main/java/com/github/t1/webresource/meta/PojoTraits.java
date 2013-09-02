package com.github.t1.webresource.meta;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

import com.github.t1.stereotypes.Annotations;
import com.google.common.collect.ImmutableMap;

/**
 * Although I generally prefer composition over inheritance, in this case I prefer the concise and intuitive syntax for
 * the client: <code>new PojoTraits(type)</code>
 */
public class PojoTraits extends ArrayList<Trait> {
    private static final long serialVersionUID = 1L;

    public PojoTraits(Class<?> type) {
        collector(type).run();
    }

    private PojoTraitAbstractCollector collector(Class<?> type) {
        AnnotatedElement annotations = Annotations.on(type);
        if (annotations.isAnnotationPresent(XmlRootElement.class)) {
            return new PojoTraitJaxbCollector(type, this, annotations);
        } else {
            return new PojoTraitDefaultCollector(type, this, annotations);
        }
    }

    public ImmutableMap<String, Trait> map() {
        ImmutableMap.Builder<String, Trait> builder = ImmutableMap.builder();
        for (Trait trait : this) {
            builder.put(trait.getName(), trait);
        }
        return builder.build();
    }
}
