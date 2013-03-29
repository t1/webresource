package com.github.t1.webresource;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Target;

/**
 * Annotate your {@link javax.persistence.Entity JPA-Entity} and add the generator to your build, and a complete REST
 * boundary will be generated for you.
 */
@Target({ TYPE })
public @interface WebResource {
    /**
     * Should the {@link javax.persistence.EntityManager EntityManager} be extended or not. This is important if you
     * have fields that are loaded lazily, because the transaction is closed before the marshaling to XML, JSON, etc. is
     * done. But it may impact performance and scalability to use extended entity managers.
     */
    public boolean extended() default true;
}
