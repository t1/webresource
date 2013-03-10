package com.github.t1.webresource;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

class WebResourceGenerator {
    private final Messager messager;
    private final Filer filer;
    private TypeElement type;

    public WebResourceGenerator(Messager messager, Filer filer) {
        this.messager = messager;
        this.filer = filer;
    }

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

    private void error(CharSequence message, Element element) {
        messager.printMessage(Kind.ERROR, message, element);
    }

    private void note(CharSequence message) {
        messager.printMessage(Kind.NOTE, message);
    }

    private JavaFileObject createSourceFile(String name, Element... elements) throws IOException {
        return filer.createSourceFile(name, elements);
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
        return new WebResourceWriter(type).run();
    }
}
