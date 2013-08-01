package com.github.t1.webresource;

import java.io.*;
import java.util.*;

class HtmlEscapeWriter extends FilterWriter {
    /** Maps the code points (as used by {@link Writer#write(int)}) to the escaped string */
    private static final Map<Integer, String> ESC = new HashMap<>();
    static {
        ESC.put((int) '&', "&amp;");
        ESC.put((int) '<', "&lt;");
        ESC.put((int) '>', "&gt;");
    }

    public HtmlEscapeWriter(Writer out) {
        super(out);
    }

    public Writer getTargetWriter() {
        return out;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        for (int i = off; i < len; i++) {
            write(cbuf[i]);
        }
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        for (int i = off; i < len; i++) {
            write(str.codePointAt(i));
        }
    }

    @Override
    public void write(int c) throws IOException {
        if (ESC.containsKey(c)) {
            String escaped = ESC.get(c);
            for (int i = 0; i < escaped.length(); i++) {
                super.write(escaped.codePointAt(i));
            }
        } else {
            super.write(c);
        }
    }
}
