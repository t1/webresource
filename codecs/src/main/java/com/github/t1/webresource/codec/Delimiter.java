package com.github.t1.webresource.codec;

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

    public void write() {
        if (first) {
            first = false;
        } else {
            try {
                writer.write(string);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
