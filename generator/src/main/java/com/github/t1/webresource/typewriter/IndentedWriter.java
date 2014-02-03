package com.github.t1.webresource.typewriter;

public class IndentedWriter {
    public final StringBuilder out = new StringBuilder();
    public int indent = 0;

    public void println(Object value) {
        indent();
        out.append(value);
        println();
    }

    public void indent() {
        for (int i = 0; i < indent; i++) {
            out.append("    ");
        }
    }

    public void println() {
        out.append('\n');
    }

    public void printIndented(Object value) {
        indent();
        char[] chars = value.toString().toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '\n') {
                println();
                if (i < chars.length - 1 && chars[i + 1] != '\n') {
                    indent();
                }
            } else {
                out.append(c);
            }
        }
    }
}
