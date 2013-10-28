package com.github.t1.webresource;

public class AbstractWriter {
    protected final StringBuilder out = new StringBuilder();
    protected int indent = 0;

    protected void append(String string) {
        appendIndent();
        out.append(string);
        nl();
    }

    protected void appendIndent() {
        for (int i = 0; i < indent; i++) {
            out.append("    ");
        }
    }

    protected void nl() {
        out.append('\n');
    }

}
