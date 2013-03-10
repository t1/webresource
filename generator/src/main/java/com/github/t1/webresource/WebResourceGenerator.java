package com.github.t1.webresource;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

class WebResourceGenerator extends AbstractGenerator {
    private TypeElement type;

    public WebResourceGenerator(Messager messager, Filer filer, Elements utils) {
        super(messager, filer, utils);
    }

    @Override
    public synchronized void process(Element element) {
        note("process " + path(element));
        this.type = (TypeElement) element;
        String targetTypeName = type.getQualifiedName() + "WebResource";
        note("Generating " + targetTypeName);

        String source = generateSource();
        try {
            JavaFileObject sourceFile = createSourceFile(targetTypeName, type);
            Writer writer = null;
            try {
                writer = sourceFile.openWriter();
                writer.write(source);
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
        } catch (IOException e) {
            error("Can't write web resource\n" + e, type);
        } finally {
            this.type = null;
        }
    }

    private String path(Element element) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Element e = element; e != null; e = e.getEnclosingElement()) {
            if (first) {
                first = false;
            } else {
                result.append('/');
            }
            result.append(e.getKind());
            result.append(':');
            result.append(e.toString());
        }
        return result.toString();
    }

    private String generateSource() {
        return new WebResourceWriter(pkg(), simple()).run();
    }

    private String pkg() {
        for (Element e = type; e != null; e = e.getEnclosingElement()) {
            if (e.getKind() == ElementKind.PACKAGE) {
                return ((PackageElement) e).getQualifiedName().toString();
            }
        }
        throw new IllegalStateException("no package for " + type);
    }

    private String simple() {
        return type.getSimpleName().toString();
    }
}
