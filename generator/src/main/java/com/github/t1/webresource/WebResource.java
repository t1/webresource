package com.github.t1.webresource;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Target;

import javax.persistence.Entity;

/**
 * Annotate your {@link Entity JPA-Entity} and add the generator to your build, and a complete REST boundary will be
 * generated for you.
 */
@Target({ TYPE })
public @interface WebResource {
    //
}
