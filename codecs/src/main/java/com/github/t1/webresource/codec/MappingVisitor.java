package com.github.t1.webresource.codec;

import com.github.t1.meta.visitor.Visitor;
import com.github.t1.webresource.util.HtmlWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
class MappingVisitor extends Visitor {
    private final HtmlWriter html;

    /** lazy write, so empty mapping writes nothing */
    boolean dlWritten = false;

    /** lazy write, so empty mapping writes nothing */
    boolean ulWritten = false;

    @Override public void enterProperty(String key) {
        log.trace("enterProperty: {}", key);
        if (!dlWritten) {
            html.open("dl").a("class", "dl-horizontal").nl();
            dlWritten = true;
        }
        html.open("dt").text(key).close("dt").nl();
    }

    @Override public void visitScalar(Object value) {
        log.trace("visitScalar: {}", value);
        html.open("dd").text(value).close("dd").nl();
    }

    @Override public void continueMapping() {
        log.trace("continueMapping {}");
        html.nl();
    }

    @Override public void leaveMapping() {
        log.trace("leaveMapping {}");
        if (dlWritten)
            html.close("dl").nl();
    }


    @Override public void enterSequence() {
        log.trace("enterSequence");
        super.enterSequence();
    }

    @Override public void enterItem() {
        log.trace("enterItem (ulWritten={})", ulWritten);
        if (!ulWritten) {
            html.open("ul").nl();
            ulWritten = true;
        }
        html.open("li");
    }

    @Override public void leaveItem() {
        log.trace("leaveItem");
        html.close("li").nl();
    }

    @Override public void leaveSequence() {
        log.trace("leaveSequence {}");
        if (ulWritten)
            html.close("ul").nl();
    }
}
