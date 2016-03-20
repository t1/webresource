package com.github.t1.webresource.util;

import com.github.t1.webresource.codec.DebugWriter;
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
        printRaw("<!DOCTYPE html>\n");
    }

    @Override public String toString() {
        return "HtmlWriter:" + out;
    }

    @SneakyThrows(IOException.class)
    private void printRaw(String text) {
        if (isNewLine)
            for (int i = 0; i < indent * 2; i++)
                out.append(' ');
        isNewLine = false;
        out.append(text);
    }

    @SneakyThrows(IOException.class)
    private void printEscaped(char c) {
        switch (c) {
        case '\"':
            out.append("&quot;");
            break;
        case '\'':
            out.append("&apos;");
            break;
        case '&':
            out.append("&amp;");
            break;
        case '<':
            out.append("&lt;");
            break;
        case '>':
            out.append("&gt;");
            break;
        default:
            out.append(c);
        }
    }

    public void nl() {
        log.trace("nl");
        isNewLine = false; // empty lines (i.e. two consecutive nl()s) don't need indent
        finishOpenTag();
        printRaw("\n");
        isNewLine = true;
    }

    public HtmlWriter open(String tagName) {
        log.trace("open {} <- {}", tagName, tags);
        finishOpenTag();
        printRaw("<" + tagName);
        tags.push(tagName);
        doneOpenTag = false;
        return this;
    }

    public HtmlWriter a(String attributeName) {
        log.trace("a {}", attributeName);
        assert !doneOpenTag : "tried to append attribute " + attributeName + " to done open tag";
        printRaw(" " + attributeName);
        return this;
    }

    public HtmlWriter a(String attributeName, Object value) {
        log.trace("a {}={}", attributeName, value);
        assert !doneOpenTag : "tried to append attribute " + attributeName + " to done open tag";
        printRaw(" " + attributeName + "=\"" + value + "\"");
        return this;
    }

    public HtmlWriter text(@NonNull Object text) {
        log.trace("text: {}", text);
        finishOpenTag();
        String string = text.toString();
        for (int i = 0; i < string.length(); i++)
            printEscaped(string.charAt(i));
        return this;
    }

    private void finishOpenTag() {
        if (!doneOpenTag) {
            indent++;
            printRaw(">");
            doneOpenTag = true;
        }
    }

    public HtmlWriter close(String expectedTag) {
        log.trace("close {} <- {}", expectedTag, tags);
        String tagName = tags.pop();
        assert expectedTag.equals(tagName) : "expected to close " + expectedTag + " but found " + tagName;
        if (doneOpenTag) {
            indent--;
            printRaw("</" + tagName + ">");
        } else {
            doneOpenTag = true;
            printRaw("/>");
        }
        return this;
    }

    @SneakyThrows(IOException.class)
    public void flush() {
        log.trace("flush");
        out.flush();
    }

}
