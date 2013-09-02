package com.github.t1.webresource.codec;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * A helper class to write objects as an url-form-encoded string... without the actual binding
 * <p>
 * TODO use {@link com.github.t1.webresource.meta.Item Item}s
 */
public class UrlEncoder {
    private Writer escaped;
    private Writer unescaped;

    public UrlEncoder(Writer out) {
        this.unescaped = out;
        this.escaped = escape(out);
    }

    private HexEscapeWriter escape(Writer out) {
        return new HexEscapeWriter(out, '%', '&', '=', ' ');
    }

    public void write(Object t) throws IOException {
        if (t == null)
            return;
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
        Delimiter delimiter = new Delimiter(unescaped, "&");
        for (Object object : list) {
            delimiter.write();
            pushEscape();
            try {
                write(object);
            } finally {
                popEscape();
            }
        }
    }

    private void pushEscape() {
        unescaped = escaped;
        escaped = escape(unescaped);
    }

    private void popEscape() {
        escaped = unescaped;
        unescaped = ((HexEscapeWriter) unescaped).getTargetWriter();
    }

    private void write(Map<?, ?> map) throws IOException {
        Delimiter delimiter = new Delimiter(unescaped, "&");
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            delimiter.write();
            write(entry.getKey());
            unescaped.write("=");
            write(entry.getValue());
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
                    write(fields.get(0).get(t));
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
        Delimiter delimiter = new Delimiter(unescaped, "&");
        for (Field field : fields) {
            delimiter.write();
            escaped.write(field.getName());
            unescaped.write("=");
            write(field.get(t));
        }
    }
}
