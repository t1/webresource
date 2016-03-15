package com.github.t1.webresource.codec;

import com.github.t1.webresource.util.StringTool;

import java.lang.reflect.*;

import static com.github.t1.webresource.util.StringTool.*;
import static com.github.t1.webresource.util.Types.*;

class TitleBuilder {
    private final String title;

    public TitleBuilder(Type type) {
        this.title = buildTitle(type);
    }

    private String buildTitle(Type type) {
        StringTool tool = empty();
        if (isGenericCollection(type)) {
            type = elementType((ParameterizedType) type);
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

    private boolean hasHtmlTitle(Type type) {
        return (type instanceof Class)
                && annotationsOn(type).isAnnotationPresent(HtmlTitle.class)
                && !annotationsOn(type).getAnnotation(HtmlTitle.class).value().isEmpty();
    }

    private HtmlTitle getHtmlTitle(Type type) { return annotationsOn(type).getAnnotation(HtmlTitle.class); }

    @Override public String toString() {
        return title;
    }
}
