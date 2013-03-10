package com.github.t1.webresource;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;
import javax.tools.*;

class WebResourceGenerator {
    private final Messager messager;
    private final Filer filer;
    private final Elements utils;

    private TypeElement type;

    public WebResourceGenerator(Messager messager, Filer filer, Elements utils) {
        this.messager = messager;
        this.filer = filer;
        this.utils = utils;
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

    protected void error(CharSequence message) {
        messager.printMessage(Kind.ERROR, message);
    }

    protected void error(CharSequence message, Element element) {
        messager.printMessage(Kind.ERROR, message, element);
    }

    protected void warn(CharSequence message) {
        messager.printMessage(Kind.WARNING, message);
    }

    protected void warn(CharSequence message, Element element) {
        messager.printMessage(Kind.WARNING, message, element);
    }

    protected void note(CharSequence message) {
        messager.printMessage(Kind.NOTE, message);
    }

    protected void note(CharSequence message, Element element) {
        messager.printMessage(Kind.NOTE, message, element);
    }

    protected JavaFileObject createSourceFile(String name, Element... elements) throws IOException {
        return filer.createSourceFile(name, elements);
    }

    protected FileObject createResourceFile(String pkg, String name, List<TypeElement> rootElements) throws IOException {
        Element[] elements = toArray(rootElements);
        return filer.createResource(StandardLocation.CLASS_OUTPUT, pkg, name, elements);
    }

    private Element[] toArray(List<TypeElement> rootElements) {
        return rootElements.toArray(new Element[rootElements.size()]);
    }

    protected String getCompoundName(TypeElement typeElement) {
        String packageName = getPackageOf(typeElement);
        String qualifiedName = typeElement.getQualifiedName().toString();
        return qualifiedName.substring(packageName.length() + 1);
    }

    protected String getPackageOf(Element element) {
        return utils.getPackageOf(element).getQualifiedName().toString();
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
