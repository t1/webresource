package com.github.t1.webresource.meta;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

import com.github.t1.stereotypes.Annotations;

/**
 * Although I generally prefer composition over inheritance, in this case I prefer the concise and intuitive syntax for
 * the client: <code>new PojoTraits(type)</code>
 */
public class PojoTraits extends ArrayList<Trait> {
    private static final long serialVersionUID = 1L;

    public static final PojoTraits EMPTY = new PojoTraits();

    private PojoTraits() {}

    public PojoTraits(Class<?> type) {
        collector(type).run();
    }

    private AbstractPojoTraitCollector collector(Class<?> type) {
        AnnotatedElement annotations = Annotations.on(type);
        if (annotations.isAnnotationPresent(XmlRootElement.class)) {
            return new JaxbPojoTraitCollector(type, this, annotations);
        } else {
            return new DefaultPojoTraitCollector(type, this, annotations);
        }
    }
}
