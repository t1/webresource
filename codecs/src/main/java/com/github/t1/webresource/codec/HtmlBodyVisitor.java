package com.github.t1.webresource.codec;

import com.github.t1.meta.visitor.VisitorDecorator;
import com.github.t1.stereotypes.Annotations;
import com.github.t1.webresource.util.*;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.UriInfo;
import java.lang.reflect.*;

@Slf4j
class HtmlBodyVisitor extends VisitorDecorator {
    private final Type rootType;
    private final HtmlWriter html;
    private final UriInfo uriInfo;

    private Boolean isHtmlPanel = null;

    HtmlBodyVisitor(Type rootType, HtmlWriter html, UriInfo uriInfo) {
        super(new MappingVisitor(html, uriInfo));
        this.rootType = rootType;
        this.html = html;
        this.uriInfo = uriInfo;
    }

    private String debugInfo() {
        if (!log.isTraceEnabled())
            return "";
        StringBuilder out = new StringBuilder();
        out.append(isSequence() ? "[sequence]" : "[mapping]");
        if (isRoot())
            out.append("[root]");
        if (isHtmlPanel())
            out.append("[panel]");
        return out.toString();
    }

    private boolean isSequence() { return getDelegate() instanceof SequenceVisitor; }

    @Override public void enterMapping() {
        log.trace("enterMapping {}", debugInfo());
        openApplicablePanel();
        super.enterMapping();
    }

    @Override public void leaveMapping() {
        log.trace("leaveMapping {}", debugInfo());
        super.leaveMapping();
        closeApplicablePanel();
    }

    @Override public void enterSequence() {
        log.trace("enterSequence {}", debugInfo());
        if (isRoot()) {
            this.setDelegate(new SequenceVisitor(html, guide().depth() + 1, uriInfo));
            log.trace("switched to sequence {}", debugInfo());
        }
        openApplicablePanel();
        super.enterSequence();
    }

    @Override public void leaveSequence() {
        log.trace("leaveSequence  {}", debugInfo());
        super.leaveSequence();
        closeApplicablePanel();
    }


    private boolean isRoot() { return guide().depth() == 0; }

    private boolean isHtmlPanel() {
        if (isHtmlPanel == null)
            isHtmlPanel = annotations().isAnnotationPresent(HtmlPanel.class);
        return isHtmlPanel;
    }

    private void openApplicablePanel() {
        if (isRoot() && isHtmlPanel()) {
            log.trace("open panel");
            html.open("div").a("class", "panel panel-default").nl();
            TitleBuilder titleBuilder = new TitleBuilder(rootType, destination());
            if (titleBuilder.hasHtmlTile() || !isSequence() && titleBuilder.hasTitleField())
                html.open("div").a("class", "panel-heading")
                        .open("h1").a("class", "panel-title")
                        .text(titleBuilder.build())
                        .close("h1")
                        .close("div").nl();
            html.open("div").a("class", "panel-body").nl();
        }
    }

    private AnnotatedElement annotations() {
        Class<?> type = (isRoot())
                ? (Class<?>) Types.nonCollectionType(rootType)
                : destination().getClass();
        return Annotations.on(type);
    }


    private void closeApplicablePanel() {
        if (isRoot() && isHtmlPanel()) {
            log.trace("close panel");
            html.close("div").nl(); // body
            html.close("div").nl(); // panel
        }
    }
}
