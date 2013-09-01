package com.github.t1.webresource;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

import com.github.t1.stereotypes.Annotations;

/**
 * Although I generally prefer composition over inheritance, in this case I prefer the concise and intuitive syntax for
 * the client: <code>new PojoTraits(type)</code>
 */
public class PojoTraits extends ArrayList<Trait> {
    private static final long serialVersionUID = 1L;

    public PojoTraits(Class<?> type) {
        if (Annotations.on(type).isAnnotationPresent(XmlRootElement.class)) {
            new JaxbTraitProvider(type, this).run();
        } else {
            new DefaultTraitProvider(type, this).run();
        }
    }
}
