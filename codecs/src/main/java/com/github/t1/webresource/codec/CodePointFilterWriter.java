package com.github.t1.webresource.codec;


import java.io.*;

/**
 * Allows you to only override {@link FileWriter#write(int)} by detouring all the other write methods.
 */
public abstract class CodePointFilterWriter extends FilterWriter {

    public CodePointFilterWriter(Writer out) {
        super(out);
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

    /** helpful to prevent recursion */
    protected void writeUnescaped(String str) throws IOException {
        for (int i = 0; i < str.length(); i++) {
            super.write(str.codePointAt(i));
        }
    }
}
