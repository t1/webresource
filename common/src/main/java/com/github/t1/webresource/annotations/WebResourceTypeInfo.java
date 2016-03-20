package com.github.t1.webresource.annotations;

public class WebResourceTypeInfo {
    public final String plural;

    public WebResourceTypeInfo(String simple) {
        this.plural = plural(simple.toLowerCase());
    }

    private String plural(String name) {
        if (name.endsWith("y"))
            return name.substring(0, name.length() - 1) + "ies";
        return name + "s";
    }
}
