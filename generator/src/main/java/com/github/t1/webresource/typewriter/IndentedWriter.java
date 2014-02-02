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
}
