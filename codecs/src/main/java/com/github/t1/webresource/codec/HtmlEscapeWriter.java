package com.github.t1.webresource.codec;

import java.io.*;
import java.util.*;

public class HtmlEscapeWriter extends CodePointFilterWriter {
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

    @Override
    public void write(int c) throws IOException {
        if (ESC.containsKey(c)) {
            writeUnescaped(ESC.get(c));
        } else {
            super.write(c);
        }
    }
}
