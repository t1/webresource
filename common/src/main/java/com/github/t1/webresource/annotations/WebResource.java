package com.github.t1.webresource.annotations;

import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * Annotate your {@link javax.persistence.Entity JPA-Entity} and add the generator to your build, and a complete REST
 * boundary will be generated for you. It will be named like the entity with <code>WebResource</code> appended.
 */
@Target({ TYPE })
public @interface WebResource {
    /**
     * Should the {@link javax.persistence.EntityManager EntityManager} be extended or not. This is important if you
     * have fields that are loaded lazily, because the transaction is closed before the the entity is marshaled to XML,
     * JSON, etc. But it may impact performance and scalability to use extended entity managers.
     */
    boolean extended() default false;

    /**
     * The path where this resource should be served from.
     * Defaults to the plural of the entity name.
     */
    String path() default "";
}
