package com.github.t1.webresource.html;

import java.io.PrintWriter;

public class Tag implements AutoCloseable {
    private final PrintWriter writer;
    private final String tagName;
    private boolean started;

    public Tag(Tag container, String tagName) {
        this(container.startedWriter(), tagName);
    }

    protected Tag(PrintWriter writer, String tagName) {
        this.writer = writer;
        this.tagName = tagName;
        this.started = false;

        writer.append('<').append(tagName);
    }

    private PrintWriter startedWriter() {
        if (!started) {
            started = true;
            writer.append('>');
        }
        return writer;
    }

    protected void attribute(String name, Object value) {
        if (started)
            throw new IllegalStateException(tagName + "-tag already started");
        if (value == null)
            return;
        writer.append(" ").append(name).append("=\"").append(value.toString()).append("\"");
    }

    public void write(String text) {
        startedWriter().append(text);
    }

    @Override
    public void close() {
        if (started) {
            writer.append("</").append(tagName).append(">\n");
        } else {
            writer.append("/>\n");
        }
    }
}
