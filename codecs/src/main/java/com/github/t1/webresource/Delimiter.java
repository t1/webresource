package com.github.t1.webresource;

import java.io.*;

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
