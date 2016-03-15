package com.github.t1.webresource.codec;

import lombok.RequiredArgsConstructor;

import java.io.*;

@RequiredArgsConstructor
public class DebugWriter extends Writer {
    private final Writer out;
    private final StringBuilder buffer = new StringBuilder();

    @Override public void write(char[] cbuf, int off, int len) throws IOException {
        buffer.append(cbuf, off, len);
        out.write(cbuf, off, len);
    }

    @Override public void flush() throws IOException { out.flush(); }

    @Override public void close() throws IOException { out.close(); }

    @Override public String toString() { return buffer.toString(); }
}
