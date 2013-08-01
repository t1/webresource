package com.github.t1.webresource;

import java.io.*;

class HexEscapeWriter extends FilterWriter {

    private final int escapeCharacter;
    private final int[] escapees;

    public HexEscapeWriter(Writer out, int escapeCharacter, int... escapees) {
        super(out);
        this.escapeCharacter = escapeCharacter;
        this.escapees = escapees;
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
        if (isEscapee(c)) {
            super.write(escapeCharacter);
            super.write(Integer.toHexString(c));
        } else {
            super.write(c);
        }
    }

    private boolean isEscapee(int c) {
        if (c == escapeCharacter) // this always has to be escaped
            return true;
        for (int i : escapees) {
            if (i == c) {
                return true;
            }
        }
        return false;
    }
}
