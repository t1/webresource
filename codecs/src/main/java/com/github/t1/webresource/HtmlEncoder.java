package com.github.t1.webresource;

import java.io.*;
import java.lang.reflect.*;
import java.net.URI;
import java.nio.file.*;
import java.util.*;

import javax.xml.bind.annotation.XmlTransient;

import lombok.Data;

import com.github.t1.stereotypes.Annotations;

/** A helper class to write objects as an html string... without the actual binding */
public class HtmlEncoder {
    @Data
    private static class Attribute {
        private final String name;
        private final String value;
    }

    private class Tag implements AutoCloseable {
        private final String name;

        public Tag(String name, Attribute... attributes) throws IOException {
            this.name = name;
            unescaped.append('<').append(name);
            for (Attribute attribute : attributes)
                unescaped.append(' ').append(attribute.name).append("='").append(attribute.value).append('\'');
            unescaped.append(">");
        }

        @Override
        public void close() throws IOException {
            unescaped.append("</").append(name).append(">\n");
        }
    }

    private final Writer escaped;
    private final Writer unescaped;
    private final Path applicationPath;
    private final Map<String, Integer> ids = new HashMap<>();

    public HtmlEncoder(Writer out, URI baseUri) {
        this.unescaped = out;
        this.applicationPath = applicationPath(baseUri);
        this.escaped = new HtmlEscapeWriter(out);
    }

    /**
     * The path of the JAX-RS base-uri starts with the resource base (often 'rest'), but we need the application base,
     * which is the first path element.
     */
    private Path applicationPath(URI baseUri) {
        return Paths.get(baseUri.getPath()).getName(0);
    }

    public void write(Object object) throws IOException {
        try (Tag html = new Tag("html")) {
            nl();
            try (Tag head = new Tag("head")) {
                writeHead(object);
            }
            try (Tag body = new Tag("body")) {
                writeBody(object);
            }
        }
    }

    private void nl() throws IOException {
        unescaped.append('\n');
    }

    private void writeHead(Object object) throws IOException {
        if (object == null || isSimple(object))
            return;
        PojoHolder pojo = new PojoHolder(object);
        writeTitle(pojo);
        writeStyleSheets(pojo);
    }

    private void writeTitle(PojoHolder pojo) throws IOException {
        String titleString = titleString(pojo);
        if (!titleString.isEmpty()) {
            try (Tag title = new Tag("title")) {
                escaped.append(titleString);
            }
        }
    }

    private String titleString(PojoHolder pojo) throws IOException {
        StringWriter titleString = new StringWriter();
        Delimiter delim = new Delimiter(titleString, " - ");
        for (PojoProperty property : pojo.properties()) {
            if (property.is(HtmlHead.class)) {
                delim.write();
                titleString.append(property.get());
            }
        }
        return titleString.toString();
    }

    private void writeStyleSheets(PojoHolder pojo) throws IOException {
        if (pojo.is(HtmlStyleSheet.class)) {
            nl();
            writeStyleSheet(pojo.get(HtmlStyleSheet.class));
        }
        if (pojo.is(HtmlStyleSheets.class)) {
            nl();
            for (HtmlStyleSheet styleSheet : pojo.get(HtmlStyleSheets.class).value()) {
                writeStyleSheet(styleSheet);
            }
        }
    }

    private void writeStyleSheet(HtmlStyleSheet styleSheet) throws IOException {
        String url = styleSheet.value();
        if (!url.startsWith("/"))
            url = "/" + applicationPath.resolve(url);
        unescaped.write("<link rel='stylesheet' href='" + url + "' type='text/css'/>\n");
    }

    private void writeBody(Object t) throws IOException {
        if (t == null)
            return;
        nl();
        if (t instanceof List) {
            write((List<?>) t);
        } else if (t instanceof Map) {
            write((Map<?, ?>) t);
        } else if (isSimple(t)) {
            escaped.write(t.toString());
        } else {
            writePojo(t);
        }
    }

    private void write(List<?> list) throws IOException {
        if (list.isEmpty())
            return;
        Object t = list.get(0);
        if (isSimple(t)) {
            writeSimpleList(list);
        } else {
            List<Field> fields = fields(t);
            switch (fields.size()) {
                case 0:
                    break;
                case 1:
                    writeOneFieldList(list, fields.get(0));
                    break;
                default:
                    writeTable(fields, list);
            }
        }
    }

    private void writeSimpleList(List<?> list) throws IOException {
        try (Tag ul = new Tag("ul")) {
            for (Object object : list) {
                try (Tag li = new Tag("li")) {
                    escaped.append(Objects.toString(object));
                }
            }
        }
    }

    private void writeOneFieldList(List<?> list, Field field) throws IOException {
        try (Tag ul = new Tag("ul")) {
            for (Object object : list) {
                try (Tag li = new Tag("li")) {
                    writeField(field, object);
                }
            }
        }
    }

    private void writeField(Field field, Object object) throws IOException {
        try {
            escaped.append(Objects.toString(field.get(object)));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("can't get field " + field + " of " + object, e);
        }
    }

    private void writeTable(List<Field> fields, List<?> list) throws IOException {
        try (Tag ul = new Tag("table")) {
            writeTableHead(fields);
            for (Object object : list) {
                writeTableRow(object, fields);
            }
        }
    }

    private void writeTableHead(List<Field> fields) throws IOException {
        try (Tag tr = new Tag("tr")) {
            for (Field field : fields) {
                try (Tag td = new Tag("td")) {
                    escaped.append(field.getName());
                }
            }
        }
    }

    private void writeTableRow(Object object, List<Field> fields) throws IOException {
        try (Tag tr = new Tag("tr")) {
            for (Field field : fields) {
                try (Tag td = new Tag("td")) {
                    writeField(field, object);
                }
            }
        }
    }

    private void write(Map<?, ?> map) throws IOException {
        Delimiter delimiter = new Delimiter(unescaped, "&");
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            delimiter.write();
            writeBody(entry.getKey());
            unescaped.write("=");
            writeBody(entry.getValue());
        }
    }

    private boolean isSimple(Object t) {
        return t instanceof String || t instanceof Number || t instanceof Boolean || t.getClass().isPrimitive();
    }

    private void writePojo(Object t) throws IOException {
        try {
            List<Field> fields = fields(t);
            switch (fields.size()) {
                case 0:
                    break;
                case 1:
                    writeBody(fields.get(0).get(t));
                    break;
                default:
                    writeFields(fields, t);
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Field> fields(Object t) {
        List<Field> result = new ArrayList<>();
        for (Field field : t.getClass().getDeclaredFields()) {
            if (!isMarshallable(field))
                continue;
            field.setAccessible(true);
            result.add(field);
        }
        return result;
    }

    private boolean isMarshallable(Field field) {
        int modifiers = field.getModifiers();
        return !Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers)
                && !Annotations.on(field).isAnnotationPresent(XmlTransient.class);
    }

    private void writeFields(List<Field> fields, Object t) throws ReflectiveOperationException, IOException {
        for (Field field : fields) {
            try (Tag div = new Tag("div")) {
                String id = id(field.getName());
                try (Tag label = new Tag("label", new Attribute("for", id))) {
                    escaped.write(field.getName());
                }
                writeValue(id, field.get(t));
            }
        }
    }

    private String id(String name) {
        Integer i = ids.get(name);
        if (i == null)
            i = 0;
        ids.put(name, i + 1);
        return name + "-" + i;
    }

    private void writeValue(String id, Object value) throws IOException {
        unescaped.append("<input id='" + id + "' type='text'");
        if (value != null)
            unescaped.append(" value='" + value + "' readonly");
        unescaped.append("/>\n");
    }
}
