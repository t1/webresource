package com.github.t1.webresource.codec;

import com.github.t1.meta.visitor.Visitor;
import com.github.t1.stereotypes.Annotations;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class HtmlBodyVisitor extends Visitor {
    private final HtmlWriter html;

    /** lazy write, so empty pojo writes nothing */
    boolean dlWritten = false;
    private boolean isSequence = false;

    @Override public void enterMapping() {
        if (isHtmlPanel()) {
            html.open("div").a("class", "panel panel-default").nl();
            html.open("div").a("class", "panel-heading")
                    .open("h1").text(new TitleBuilder(destination().getClass())).close("h1")
                    .close("div").nl();
        }
        if (isSequence) {
            html.text(destination().toString());
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
            html.open("dl").a("class", "dl-horizontal").nl();
            dlWritten = true;
        }
        html.open("dt").text(key).close("dt").nl();
    }

    @Override public void visitScalar(Object value) {
        if (isSequence)
            return;
        html.open("dd").text(value).close("dd").nl();
    }

    @Override public void continueMapping() {
        if (isSequence)
            return;
        html.nl();
    }

    @Override public void leaveMapping() {
        if (isSequence)
            return;
        if (dlWritten)
            html.close("dl").nl();
        if (isHtmlPanel()) {
            html.close("div").nl();
        }
    }

    @Override public void enterSequence() {
        this.isSequence = true;
        html.open("ul").nl();
    }

    @Override public void enterItem() {
        html.open("li");
    }

    @Override public void leaveItem() { html.close("li").nl(); }

    @Override public void leaveSequence() {
        html.close("ul").nl();
    }
}
