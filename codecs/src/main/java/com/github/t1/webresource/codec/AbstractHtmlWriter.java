package com.github.t1.webresource.codec;

import java.io.*;

import javax.inject.Inject;

import lombok.Data;

import com.github.t1.webresource.meta.*;

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
    Writer out;
    @Inject
    IdGenerator ids;
    @Inject
    UriResolver uriResolver;

    public abstract void write(Item item);

    private void init(AbstractHtmlWriter that) {
        that.out = this.out;
        that.uriResolver = this.uriResolver;
        that.ids = this.ids;
    }

    private void writeTo(Class<? extends AbstractHtmlWriter> type, Item item) {
        try {
            writeTo(type.newInstance(), item);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeTo(AbstractHtmlWriter writer, Item item) {
        init(writer);
        writer.write(item);
    }

    public void writeHead(Item item) {
        writeTo(HtmlHeadWriter.class, item);
    }

    public void writeBody(Item item) {
        writeTo(HtmlBodyWriter.class, item);
    }

    public void writeForm(Item item) {
        writeTo(HtmlFormWriter.class, item);
    }

    public void writeList(Item item) {
        writeTo(HtmlListWriter.class, item);
    }

    public void writeField(Item item, Trait trait, String id) {
        writeTo(new HtmlFieldWriter(trait, id), item);
    }

    public void writeLink(Item item, String id) {
        writeTo(new HtmlLinkWriter(id), item);
    }

    public void writeTable(Item item) {
        writeTo(HtmlTableWriter.class, item);
    }

    protected void write(String text) {
        try {
            out.write(text);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void write(Object object) {
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

    protected String id(Trait trait) {
        return id(new FieldName(trait).toString());
    }

    protected String id(String name) {
        return ids.get(name);
    }
}
