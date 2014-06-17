package com.github.t1.webresource.codec2;

import java.io.*;
import java.text.*;

public class HtmlLiteralWriter {

    private final Writer out;

    public HtmlLiteralWriter(Writer out) {
        this.out = out;
    }

    public void print(String text) {
        try {
            printOrFail(text);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void printOrFail(String text) throws IOException {
        CharacterIterator iter = new StringCharacterIterator(text);
        boolean newline = true;
        for (char c = iter.first(); c != CharacterIterator.DONE; c = iter.next()) {
            if (newline) {
                if (c == ' ') {
                    out.write("&nbsp;");
                } else if (c == '\t') {
                    out.write("&nbsp;&nbsp;");
                } else if (c != '\n') {
                    newline = false;
                }
            } else {
                if (c == '\n') {
                    out.write("<br/>");
                    newline = true;
                }
            }
            if (c == '&') {
                out.write("&amp;");
            } else if (c == '<') {
                out.write("&lt;");
            } else if (c == '>') {
                out.write("&gt;");
            } else {
                out.write(c);
            }
        }
    }
}
