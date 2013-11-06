package com.github.t1.webresource.codec;

import java.io.*;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import lombok.*;

import com.github.t1.webresource.meta.*;

@RequestScoped
public class HtmlOut {
    @Data
    public static class Attribute {
        private final String name;
        private final String value;
    }

    public class Tag implements AutoCloseable {
        private final String name;

        public Tag(String name, Attribute... attributes) {
            this.name = name;
            try {
                out.append('<').append(name);
                for (Attribute attribute : attributes)
                    out.append(' ').append(attribute.getName()).append("='").append(attribute.getValue()).append('\'');
                out.append(">");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void close() {
            try {
                out.append("</").append(name).append(">\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Setter
    private Writer out;

    @Inject
    Instance<HtmlListWriter> htmlListWriter;
    @Inject
    Instance<HtmlTableWriter> htmlTableWriter;
    @Inject
    Instance<HtmlFieldWriter> htmlFieldWriter;
    @Inject
    Instance<HtmlLinkWriter> htmlLinkWriter;

    public void writeList(Item item) {
        htmlListWriter.get().write(item);
    }

    public void writeTable(Item item) {
        htmlTableWriter.get().write(item);
    }

    public void writeField(Item item, Trait trait, String id) {
        htmlFieldWriter.get().write(item, trait, id);
    }

    public void writeLink(Item item, String id) {
        htmlLinkWriter.get().write(item, id);
    }

    public void write(String text) {
        if (out == null)
            throw new NullPointerException("no out in " + getClass().getSimpleName());
        try {
            out.write(text);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeObject(Object object) {
        if (object != null) {
            write(object.toString());
        }
    }

    public void write(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        while (true) {
            String line = readLine(reader);
            if (line == null)
                break;
            write(line);
            nl();
        }
    }

    private String readLine(BufferedReader reader) {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(Exception e) {
        e.printStackTrace(new PrintWriter(out));
    }

    public void writeEscapedObject(Object object) {
        writeEscaped(object.toString());
    }

    public void writeEscaped(String string) {
        try {
            new HtmlEscapeWriter(out).write(string);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void nl() {
        write("\n");
    }

    public Tag tag(String name, Attribute... attributes) {
        return new Tag(name, attributes);
    }
}
