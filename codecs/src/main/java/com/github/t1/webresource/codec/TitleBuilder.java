package com.github.t1.webresource.codec;

import com.github.t1.stereotypes.Annotations;
import com.github.t1.webresource.util.StringTool;

import java.lang.reflect.*;
import java.util.Collection;

import static com.github.t1.webresource.util.StringTool.*;

class TitleBuilder {
    private final String title;

    public TitleBuilder(Type type) {
        this.title = buildTitle(type);
    }

    private String buildTitle(Type type) {
        StringTool tool = empty();
        if (isCollection(type)) {
            type = elementType(type);
            if (hasHtmlTitle(type) && !getHtmlTitle(type).plural().isEmpty())
                return getHtmlTitle(type).plural();
            tool = tool.and(StringTool::pluralize);
        }
        String typeName;
        if (type instanceof Class) {
            Class<?> clazz = (Class<?>) type;
            if (hasHtmlTitle(clazz)) {
                typeName = getHtmlTitle(clazz).value();
            } else {
                tool = tool.and(camelToWords());
                typeName = clazz.getSimpleName();
            }
        } else {
            typeName = type.getTypeName();
            tool = tool.and(camelToWords());
        }
        return tool.apply(typeName);
    }

    private boolean isCollection(Type type) {
        return type instanceof ParameterizedType && Collection.class.isAssignableFrom(raw(type));
    }

    private Class<?> raw(Type type) {
        return (Class<?>) ((ParameterizedType) type).getRawType();
    }

    private Type elementType(Type type) {
        return ((ParameterizedType) type).getActualTypeArguments()[0];
    }

    private boolean hasHtmlTitle(Type type) {
        return (type instanceof Class) && annotationsOn(type).isAnnotationPresent(HtmlTitle.class);
    }

    private HtmlTitle getHtmlTitle(Type type) { return annotationsOn(type).getAnnotation(HtmlTitle.class); }

    private AnnotatedElement annotationsOn(Type type) { return Annotations.on((Class<?>) type); }

    @Override public String toString() {
        return title;
    }
}
