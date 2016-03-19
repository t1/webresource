package com.github.t1.webresource.codec;

import com.github.t1.meta.Property;
import com.github.t1.meta.visitor.Visitor;
import com.github.t1.webresource.Lazy;
import com.github.t1.webresource.util.HtmlWriter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

@Slf4j
class MappingVisitor extends Visitor {
    private final HtmlWriter html;

    private final Lazy dl;
    private final Lazy ul;
    private final Lazy continueNl;

    private Property property;

    MappingVisitor(HtmlWriter html) {
        this.html = html;
        this.dl = new Lazy(() -> html.open("dl").a("class", "dl-horizontal").nl());
        this.ul = new Lazy(() -> html.open("ul").nl());
        this.continueNl = new Lazy(html::nl).setRan(true);
    }

    @Override public void enterProperty(Property property) {
        log.trace("enterProperty: {}", property);
        this.property = property;
        if (skipProperty())
            return;
        continueNl.run();
        dl.run();
        html.open("dt").text(property.name()).close("dt").nl();
    }

    private boolean skipProperty() {
        return property.isAnnotationPresent(XmlTransient.class)
                || property.isAnnotationPresent(Id.class)
                || property.isAnnotationPresent(Version.class);
    }

    @Override public void visitScalar(Object value) {
        log.trace("visitScalar: {}", value);
        if (skipProperty())
            return;
        html.open("dd").text(value).close("dd").nl();
    }

    @Override public void leaveProperty() {
        log.trace("leaveProperty");
        this.property = null;
    }

    @Override public void continueMapping() {
        log.trace("continueMapping {}");
        if (dl.ran())
            continueNl.reset();
    }

    @Override public void leaveMapping() {
        log.trace("leaveMapping {}");
        if (dl.ran())
            html.close("dl").nl();
        dl.reset();
    }


    @Override public void enterSequence() {
        log.trace("enterSequence");
        super.enterSequence();
    }

    @Override public void enterItem() {
        log.trace("enterItem (ulWritten={})", ul.ran());
        ul.run();
        html.open("li");
    }

    @Override public void leaveItem() {
        log.trace("leaveItem");
        html.close("li").nl();
    }

    @Override public void leaveSequence() {
        log.trace("leaveSequence {}");
        if (ul.ran())
            html.close("ul").nl();
        ul.reset();
    }
}
