package com.github.t1.webresource.codec2;

import java.net.URI;
import java.util.*;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class HtmlPartResover {
    @Inject
    HtmlListPartWriter listWriter;
    @Inject
    HtmlMapPartWriter mapWriter;
    @Inject
    HtmlLinkPartWriter uriWriter;
    @Inject
    ToStringPartWriter objectWriter;

    @SuppressWarnings("unchecked")
    public <T> HtmlPartWriter<T> of(T item) {
        return (HtmlPartWriter<T>) resolve(item);
    }

    private HtmlPartWriter<?> resolve(Object item) {
        if (item instanceof List) {
            return listWriter;
        } else if (item instanceof Map) {
            return mapWriter;
        } else if (item instanceof URI) {
            return uriWriter;
        } else {
            return objectWriter;
        }
    }
}
