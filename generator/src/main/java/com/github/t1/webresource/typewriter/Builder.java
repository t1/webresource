package com.github.t1.webresource.typewriter;

import java.util.Set;

public interface Builder {

    /** the types used by this builder, i.e. that need to be imported */
    Set<Class<?>> types();

}