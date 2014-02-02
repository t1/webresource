package com.github.t1.webresource.typewriter;

import java.util.List;

public interface Builder {

    /** the types used by this builder, i.e. that need to be imported */
    List<Class<?>> types();

}