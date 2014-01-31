package com.github.t1.webresource.typewriter;

public class IndentedWriter {
    public final StringBuilder out = new StringBuilder();
    public int indent = 0;

    public void append(Object value) {
        indent();
        out.append(value);
        nl();
    }

    public void indent() {
        for (int i = 0; i < indent; i++) {
            out.append("    ");
        }
    }

    public void nl() {
        out.append('\n');
    }
}
