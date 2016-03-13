package com.github.t1.webresource.codec;

import com.github.t1.log.shaded.stereotypes.Annotations;
import com.github.t1.meta.visitor.Visitor;
import lombok.*;

import java.io.*;

@RequiredArgsConstructor
class HtmlBodyVisitor extends Visitor {
    private final Writer out;
    /** lazy write, so empty pojo writes nothing */
    boolean dlWritten = false;
    private boolean isSequence = false;

    @SneakyThrows(IOException.class)
    private void println(String text) {
        out.append(text).append('\n');
    }

    @SneakyThrows(IOException.class)
    private Writer print(String text) {
        return out.append(text);
    }

    @Override public void enterMapping() {
        if (isHtmlPanel()) {
            println("    <div class=\"panel panel-default\">");
            println("      <div class=\"panel-heading\"><h1>" + new TitleBuilder(destination().getClass()).toString()
                    + "</h1></div>");
        }
        if (isSequence) {
            print(destination().toString());
        }
    }

    private boolean isHtmlPanel() {
        return Annotations.on(destinationType()).isAnnotationPresent(HtmlPanel.class);
    }

    private Object destination() { return guide().getDestination(); }

    private Class<?> destinationType() { return destination().getClass(); }

    @Override public void enterProperty(String key) {
        if (isSequence)
            return;
        if (!dlWritten) {
            println("    <dl class=\"dl-horizontal\">");
            dlWritten = true;
        }
        println("      <dt>" + key + "</dt>");
    }

    @Override public void visitScalar(Object value) {
        if (isSequence)
            return;
        println("      <dd>" + value + "</dd>");
    }

    @Override public void continueMapping() {
        if (isSequence)
            return;
        println("");
    }

    @Override public void leaveMapping() {
        if (isSequence)
            return;
        if (dlWritten)
            println("    </dl>");
        if (isHtmlPanel()) {
            println("    </div>");
            println("    </div>");
        }
    }

    @Override public void enterSequence() {
        this.isSequence = true;
        println("    <ul>");
    }

    @Override public void enterItem() {
        print("      <li>");
    }

    @Override public void leaveItem() {
        println("</li>");
    }

    @Override public void leaveSequence() {
        println("    </ul>");
    }
}
