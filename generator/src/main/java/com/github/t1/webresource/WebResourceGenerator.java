package com.github.t1.webresource;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;

import net.java.messageapi.processor.TemplateGenerator;

public class WebResourceGenerator extends TemplateGenerator {

    public WebResourceGenerator(Messager messager, Filer filer, Elements utils) {
        super(messager, filer, utils, "/WebResource.template");
    }

    @Override
    protected String getTargetTypeName() {
        return type.getQualifiedName() + "WebResource";
    }

    @Override
    protected String replaceVariable(String variable) {
        if ("simple".equals(variable)) {
            return getSimple();
        } else if ("lower".equals(variable)) {
            return getSimple().toLowerCase();
        } else if ("pkg".equals(variable)) {
            return getPackage();
        } else {
            throw new RuntimeException("invalid variable name " + variable);
        }
    }

    private String getSimple() {
        return type.getSimpleName().toString();
    }

    private String getPackage() {
        for (Element e = type; e != null; e = e.getEnclosingElement()) {
            if (e.getKind() == ElementKind.PACKAGE) {
                return ((PackageElement) e).getQualifiedName().toString();
            }
        }
        throw new IllegalStateException("no package for " + type);
    }
}
