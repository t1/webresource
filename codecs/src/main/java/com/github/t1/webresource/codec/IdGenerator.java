package com.github.t1.webresource.codec;

import java.util.*;

public class IdGenerator {

    private final Map<String, Integer> map = new HashMap<>();

    public String get(String name) {
        Integer i = map.get(name);
        if (i == null)
            i = 0;
        map.put(name, i + 1);
        return name + "-" + i;
    }
}
