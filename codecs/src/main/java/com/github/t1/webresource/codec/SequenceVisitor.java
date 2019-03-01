package com.github.t1.webresource.codec;

import com.github.t1.meta.visitor.*;
import com.github.t1.webresource.annotations.WebResourceKey;
import com.github.t1.webresource.util.HtmlWriter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Id;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Field;
import java.net.URI;

@Slf4j
class SequenceVisitor extends VisitorDecorator {
    private final HtmlWriter html;
    private final int myDepth;
    private final UriInfo uriInfo;

    SequenceVisitor(HtmlWriter html, int myDepth, UriInfo uriInfo) {
        super(new Visitor() {});
        this.html = html;
        this.myDepth = myDepth;
        this.uriInfo = uriInfo;
    }

    /** lazy write, so empty sequence writes nothing */
    private boolean ulWritten = false;

    @Override public void enterSequence() {
        log.trace("enterSequence depth {}: {}: {}", myDepth, destination().getClass(), destination());
        if (subSequence()) {
            this.setDelegate(new SequenceVisitor(html, myDepth + 1, uriInfo));
            log.trace("pushed nested sequence");
        }
        super.enterSequence();
    }

    private boolean subSequence() {return guide().depth() > myDepth;}

    @Override public void enterItem(Object item) {
        log.trace("enterItem: {}", destination());
        if (!ulWritten) {
            ulWritten = true;
            html.open("ul").nl();
        }
        html.open("li");
    }

    @Override public void enterMapping() {
        log.trace("enterMapping: {}", destination());
        Field refField = findRefField();
        if (refField != null)
            html.open("a").a("href", itemLink(refField));
        html.text(itemString());
        if (refField != null)
            html.close("a");
    }

    private Field findRefField() {
        for (Field field : destination().getClass().getDeclaredFields())
            if (field.isAnnotationPresent(WebResourceKey.class))
                return field;
        for (Field field : destination().getClass().getDeclaredFields())
            if (field.isAnnotationPresent(Id.class))
                return field;
        return null;
    }

    @SneakyThrows(ReflectiveOperationException.class)
    private URI itemLink(Field refField) {
        refField.setAccessible(true);
        Object ref = refField.get(destination());
        if (ref == null)
            return null;
        String uri = uriInfo.getRequestUri().toString();
        log.debug("build item link from request uri: {}", uri);
        if (uri.endsWith(".html"))
            uri = uri.substring(0, uri.length() - 5);
        return URI.create(uri + "/" + ref + ".html");
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
