package com.github.t1.webresource.codec;

import com.github.t1.meta.visitor.Visitor;
import com.github.t1.stereotypes.Annotations;
import com.github.t1.webresource.util.Types;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.*;

@RequiredArgsConstructor
class HtmlBodyVisitor extends Visitor {
    private final Type rootType;
    private final HtmlWriter html;

    /** lazy write, so empty pojo writes nothing */
    boolean dlWritten = false;
    private boolean isSequence = false;
    private Boolean isHtmlPanel = null;

    @Override public void enterMapping() {
        if (isSequence) {
            html.text(destination().toString());
        } else if (isHtmlPanel()) {
            panel();
        }
    }

    private boolean isHtmlPanel() {
        if (isHtmlPanel == null)
            isHtmlPanel = annotations().isAnnotationPresent(HtmlPanel.class);
        return isHtmlPanel;
    }

    private void panel() {
        html.open("div").a("class", "panel panel-default").nl();
        if (hasTitle())
            html.open("div").a("class", "panel-heading")
                    .open("h1").text(new TitleBuilder(rootType)).close("h1")
                    .close("div").nl();
        html.open("div").a("class", "panel-body").nl();
    }

    private boolean hasTitle() {
        return annotations().isAnnotationPresent(HtmlTitle.class);
    }

    private AnnotatedElement annotations() {
        Class<?> type = (guide().depth() == 0)
                ? (Class<?>) Types.nonCollectionType(rootType)
                : destination().getClass();
        return Annotations.on(type);
    }

    private Object destination() { return guide().destination(); }

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
        closePanel();
    }

    private void closePanel() {
        if (isHtmlPanel()) {
            html.close("div").nl(); // body
            html.close("div").nl(); // panel
        }
    }

    @Override public void enterSequence() {
        this.isSequence = true;
        if (isHtmlPanel())
            panel();
        html.open("ul").nl();
    }

    @Override public void enterItem() {
        html.open("li");
    }

    @Override public void leaveItem() { html.close("li").nl(); }

    @Override public void leaveSequence() {
        html.close("ul").nl();
        closePanel();
    }
}
