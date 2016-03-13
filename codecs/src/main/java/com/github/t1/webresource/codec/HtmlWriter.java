package com.github.t1.webresource.codec;

import lombok.*;

import java.io.*;
import java.util.Stack;

@RequiredArgsConstructor
public class HtmlWriter {
    private static final String SPACES = "                                                                            "
            + "                                                                                                       "
            + "                                                                                                       "
            + "                                                                                                       ";
    private final Writer out;
    private final Stack<String> tags = new Stack<>();
    private boolean doneOpenTag = true;
    private boolean isNewLine = true;
    private int indent = 0;

    @SneakyThrows(IOException.class)
    private void print(String text) {
        if (isNewLine)
            out.append(SPACES.substring(0, indent * 2));
        isNewLine = false;
        out.append(text);
    }

    public void nl() {
        isNewLine = false; // empty lines (i.e. two consecutive nl()s) don't need indent
        finishOpenTag();
        print("\n");
        isNewLine = true;
    }

    public HtmlWriter open(String tagName) {
        finishOpenTag();
        print("<" + tagName);
        tags.push(tagName);
        doneOpenTag = false;
        return this;
    }

    public HtmlWriter a(String attributeName, Object value) {
        assert !doneOpenTag : "tried to append attribute " + attributeName + " to done open tag";
        print(" " + attributeName + "=\"" + value + "\"");
        return this;
    }

    public HtmlWriter text(@NonNull Object text) {
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
        out.flush();
    }
}
