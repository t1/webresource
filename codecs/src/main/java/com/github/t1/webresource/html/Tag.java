package com.github.t1.webresource.html;

import java.io.PrintWriter;

public class Tag implements AutoCloseable {
    private final PrintWriter writer;
    private final String tagName;
    private final boolean nl;

    private boolean started;

    /**
     * @param nl
     *            print a newline after the opening tag
     */
    public Tag(Tag container, String tagName, boolean nl) {
        this(container.startedWriter(), tagName, nl);
    }

    protected Tag(PrintWriter writer, String tagName, boolean nl) {
        this.writer = writer;
        this.tagName = tagName;
        this.nl = nl;
        this.started = false;

        writer.append('<').append(tagName);
    }

    private PrintWriter startedWriter() {
        if (!started) {
            started = true;
            writer.append('>');
            if (nl) {
                writer.append('\n');
            }
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
