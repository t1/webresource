package com.github.t1.webresource.codec;

import java.io.*;

public class HexEscapeWriter extends CodePointFilterWriter {

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
