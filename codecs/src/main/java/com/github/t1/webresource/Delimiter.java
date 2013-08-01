package com.github.t1.webresource;

import java.io.*;

/**
 * Writes nothing in the first call to {@link #write()}, but the delimiter string in subsequent calls. E.g.:
 * 
 * <pre>
 * Delimiter delim = new Delimiter(out, &quot;, &quot;);
 * for (String item : list) {
 *     delim.write();
 *     out.write(item);
 * }
 * </pre>
 */
public class Delimiter {

    private final Writer writer;
    private final String string;

    private boolean first = true;

    public Delimiter(Writer writer, String string) {
        this.writer = writer;
        this.string = string;
    }

    public void write() throws IOException {
        if (first) {
            first = false;
        } else {
            writer.write(string);
        }
    }
}
