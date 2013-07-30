package com.github.t1.webresource;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import lombok.Data;

/** A helper class to write objects as an html string... without the actual binding */
class HtmlEncoder {
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
    private final Map<String, Integer> ids = new HashMap<>();

    public HtmlEncoder(Writer out) {
        this.unescaped = out;
        this.escaped = escape(out);
    }

    private HexEscapeWriter escape(Writer out) {
        return new HexEscapeWriter(out, '&', '<', '>');
    }

    public void write(Object t) throws IOException {
        try (Tag html = new Tag("html")) {
            nl();
            try (Tag head = new Tag("head")) {
                writeHead(t);
            }
            try (Tag body = new Tag("body")) {
                writeBody(t);
            }
        }
    }

    private void nl() throws IOException {
        unescaped.append('\n');
    }

    private void writeHead(Object t) throws IOException {
        // write title
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
        try (Tag ul = new Tag("ul")) {
            for (Object object : list) {
                try (Tag li = new Tag("li")) {
                    writeBody(object);
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
        return !Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers);
    }

    private void writeFields(List<Field> fields, Object t) throws ReflectiveOperationException, IOException {
        try (Tag table = new Tag("table")) {
            for (Field field : fields) {
                try (Tag tr = new Tag("tr")) {
                    String id = id(field.getName());
                    try (Tag td = new Tag("td")) {
                        try (Tag label = new Tag("label", new Attribute("for", id))) {
                            escaped.write(field.getName());
                        }
                    }
                    try (Tag td = new Tag("td")) {
                        writeValue(id, field.get(t));
                    }
                }
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
