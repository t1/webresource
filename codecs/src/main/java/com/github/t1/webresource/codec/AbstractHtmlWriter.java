package com.github.t1.webresource.codec;

import java.io.*;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import lombok.Data;

import com.github.t1.webresource.meta.*;

@RequestScoped
public abstract class AbstractHtmlWriter {
    @Data
    protected static class Attribute {
        private final String name;
        private final String value;
    }

    protected class Tag implements AutoCloseable {
        private final String name;

        public Tag(String name, Attribute... attributes) {
            this.name = name;
            try {
                out.append('<').append(name);
                for (Attribute attribute : attributes)
                    out.append(' ').append(attribute.name).append("='").append(attribute.value).append('\'');
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

    @Inject
    @HtmlWriterQualifier
    Writer out;

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

    protected void write(String text) {
        if (out == null)
            throw new NullPointerException("no out in " + getClass().getSimpleName());
        try {
            out.write(text);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void writeObject(Object object) {
        if (object != null) {
            write(object.toString());
        }
    }

    protected void write(InputStream inputStream) {
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

    protected void write(Exception e) {
        e.printStackTrace(new PrintWriter(out));
    }

    protected Writer escaped() {
        return new HtmlEscapeWriter(out);
    }

    protected void nl() {
        write("\n");
    }
}
