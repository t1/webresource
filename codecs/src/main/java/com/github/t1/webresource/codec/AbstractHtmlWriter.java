package com.github.t1.webresource.codec;

import java.io.*;

import lombok.Data;

public class AbstractHtmlWriter {
    @Data
    protected static class Attribute {
        private final String name;
        private final String value;
    }

    protected class Tag implements AutoCloseable {
        private final String name;

        public Tag(String name, Attribute... attributes) throws IOException {
            this.name = name;
            out.append('<').append(name);
            for (Attribute attribute : attributes)
                out.append(' ').append(attribute.name).append("='").append(attribute.value).append('\'');
            out.append(">");
        }

        @Override
        public void close() throws IOException {
            out.append("</").append(name).append(">\n");
        }
    }

    protected final HtmlWriter out;

    public AbstractHtmlWriter(HtmlWriter out) {
        this.out = out;
    }

    protected Writer escaped() {
        return new HtmlEscapeWriter(out);
    }

    protected void nl() throws IOException {
        out.append('\n');
    }
}
