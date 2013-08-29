package com.github.t1.webresource;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

import com.github.t1.stereotypes.Annotations;

/**
 * Although I generally prefer composition over inheritance, in this case I prefer the concise and intuitive syntax for
 * the client: <code>new PojoProperties(type)</code>
 */
public class PojoProperties extends ArrayList<Property> {
    private static final long serialVersionUID = 1L;

    public PojoProperties(Class<?> type) {
        if (Annotations.on(type).isAnnotationPresent(XmlRootElement.class)) {
            new PojoPropertiesJaxbStrategy(type, this).run();
        } else {
            new PojoPropertiesDefaultStrategy(type, this).run();
        }
    }
}
