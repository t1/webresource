package com.github.t1.webresource.codec;

import com.github.t1.stereotypes.Annotations;
import com.github.t1.webresource.tools.StringTool;
import lombok.SneakyThrows;

import java.lang.reflect.*;
import java.util.function.Function;

import static com.github.t1.webresource.tools.StringTool.*;
import static com.github.t1.webresource.util.Types.*;

class TitleBuilder {
    private final Type type;
    private final Object pojo;
    private final boolean isGenericCollection;

    private StringTool tool = StringTool.empty();

    TitleBuilder(Type type, Object pojo) {
        this.isGenericCollection = isGenericCollection(type);
        this.type = (isGenericCollection) ? elementType((ParameterizedType) type) : type;
        this.pojo = pojo;
    }

    public String build() {
        String result = doBuild();
        return tool.apply(result);
    }

    boolean hasHtmlTile() {
        return getHtmlTitle() != null;
    }

    boolean hasTitleField() {
        return findHtmlTitleField() != null;
    }

    private String doBuild() {
        if (isGenericCollection) {
            if (getHtmlTitle(HtmlTitle::plural) != null)
                return getHtmlTitle(HtmlTitle::plural);
            tool = tool.and(StringTool::pluralize);
        }
        Field htmlTitleField = findHtmlTitleField();
        if (htmlTitleField != null)
            return getString(htmlTitleField);
        else if (getHtmlTitle(HtmlTitle::value) != null)
            return getHtmlTitle(HtmlTitle::value);
        tool = tool.and(camelToWords());
        return getTypeName();
    }

    private String getHtmlTitle(Function<HtmlTitle, String> function) {
        HtmlTitle htmlTitle = getHtmlTitle();
        if (htmlTitle == null)
            return null;
        String value = function.apply(htmlTitle);
        return (value.isEmpty()) ? null : value;
    }

    private HtmlTitle getHtmlTitle() {
        if (!(type instanceof Class))
            return null;
        HtmlTitle htmlTitle = Annotations.on((Class<?>) type).getAnnotation(HtmlTitle.class);
        if (htmlTitle == null)
            return null;
        return htmlTitle;
    }

    private Field findHtmlTitleField() {
        if (!isGenericCollection)
            for (Field field : ((Class<?>) type).getDeclaredFields())
                if (field.isAnnotationPresent(HtmlTitle.class))
                    return field;
        return null;
    }

    @SneakyThrows(ReflectiveOperationException.class)
    private String getString(Field field) {
        field.setAccessible(true);
        Object value = field.get(pojo);
        return (value == null) ? "" : value.toString();
    }

    private String getTypeName() {
        String typeName;
        if (type instanceof Class)
            typeName = ((Class<?>) type).getSimpleName();
        else
            typeName = type.getTypeName();
        return typeName;
    }
}
