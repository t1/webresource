package com.github.t1.webresource.codec;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Stack;

@Slf4j
public class HtmlWriter {
    private final Writer out;
    private final Stack<String> tags = new Stack<>();
    private boolean doneOpenTag = true;
    private boolean isNewLine = true;
    private int indent = 0;

    public HtmlWriter(Writer out) {
        this.out = (log.isDebugEnabled()) ? new DebugWriter(out) : out;
    }

    @Override public String toString() {
        return "HtmlWriter:" + out;
    }

    @SneakyThrows(IOException.class)
    private void print(String text) {
        if (isNewLine)
            for (int i = 0; i < indent * 2; i++)
                out.append(' ');
        isNewLine = false;
        out.append(text);
    }

    public void nl() {
        log.trace("nl");
        isNewLine = false; // empty lines (i.e. two consecutive nl()s) don't need indent
        finishOpenTag();
        print("\n");
        isNewLine = true;
    }

    public HtmlWriter open(String tagName) {
        log.trace("open {}", tagName);
        finishOpenTag();
        print("<" + tagName);
        tags.push(tagName);
        doneOpenTag = false;
        return this;
    }

    public HtmlWriter a(String attributeName, Object value) {
        log.trace("a {}={}", attributeName, value);
        assert !doneOpenTag : "tried to append attribute " + attributeName + " to done open tag";
        print(" " + attributeName + "=\"" + value + "\"");
        return this;
    }

    public HtmlWriter text(@NonNull Object text) {
        log.trace("text: {}", text);
        finishOpenTag();
        print(text.toString()); // TODO escape markup
        return this;
    }

    private void finishOpenTag() {
        if (!doneOpenTag) {
            indent++;
            print(">");
            doneOpenTag = true;
        }
    }

    public HtmlWriter close(String expectedTag) {
        log.trace("close {}", expectedTag);
        String tagName = tags.pop();
        assert expectedTag.equals(tagName) : "expected to close " + expectedTag + " but found " + tagName;
        if (doneOpenTag) {
            indent--;
            print("</" + tagName + ">");
        } else {
            doneOpenTag = true;
            print("/>");
        }
        return this;
    }

    @SneakyThrows(IOException.class)
    public void flush() {
        log.trace("flush");
        out.flush();
    }

}
