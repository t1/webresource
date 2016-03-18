package com.github.t1.webresource.codec;

import com.github.t1.meta.visitor.*;
import com.github.t1.webresource.util.HtmlWriter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class SequenceVisitor extends VisitorDecorator {
    private final HtmlWriter html;
    private final int myDepth;

    public SequenceVisitor(HtmlWriter html, int myDepth) {
        super(new Visitor() {});
        this.html = html;
        this.myDepth = myDepth;
    }

    /** lazy write, so empty sequence writes nothing */
    boolean ulWritten = false;

    @Override public void enterSequence() {
        log.trace("enterSequence depth {}: {}: {}", myDepth, destination().getClass(), destination());
        if (subSequence()) {
            this.setDelegate(new SequenceVisitor(html, myDepth + 1));
            log.trace("pushed nested sequence");
        }
        super.enterSequence();
    }

    private boolean subSequence() {return guide().depth() > myDepth;}

    @Override public void enterItem() {
        log.trace("enterItem: {}", destination());
        if (!ulWritten) {
            ulWritten = true;
            html.open("ul").nl();
        }
        html.open("li");
    }

    @Override public void enterMapping() {
        log.trace("enterMapping: {}", destination());
        html.text(itemString());
    }

    private String itemString() {
        TitleBuilder titleBuilder = new TitleBuilder(destination().getClass(), destination());
        return titleBuilder.hasTitleField() ? titleBuilder.build() : destination().toString();
    }

    @Override public void leaveMapping() {
        log.trace("leaveMapping: {}", destination());
    }

    @Override public void leaveItem() {
        log.trace("leaveItem: {}", destination());
        html.close("li").nl();
    }

    @Override public void leaveSequence() {
        log.trace("leaveSequence depth {} ulWritten:{} subSequence:{}\n{}: {}", myDepth, ulWritten, subSequence(),
                destination().getClass(), destination());
        if (ulWritten)
            html.close("ul").nl();
        if (subSequence()) {
            log.trace("pop nested sequence");
            setDelegate(new Visitor() {});
        }
        super.leaveSequence();
    }
}
