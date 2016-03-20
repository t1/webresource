package com.github.t1.webresource.annotations;

import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * Turns the annotated field (or all fields, if annotated to the class) into a sub-resource, i.e. you can access it with
 * a path like <code>.../person/123/category</code>.
 */
@Target({ FIELD, TYPE })
public @interface WebSubResource {}
