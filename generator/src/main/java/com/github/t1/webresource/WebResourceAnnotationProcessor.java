package com.github.t1.webresource;

import com.github.t1.webresource.annotations.WebResource;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;
import java.io.*;
import java.util.*;

/**
 * The annotation processor that generates the REST bindings
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationClasses(WebResource.class)
public class WebResourceAnnotationProcessor extends AbstractProcessor2 {

    private Messager messager;
    private Filer filer;
    private TypeElement type;
    private final List<String> index = new ArrayList<>();

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);

        this.messager = getMessager();
        this.filer = env.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        note("start round (final=" + roundEnv.processingOver() + ")");
        for (Element webResource : roundEnv.getElementsAnnotatedWith(WebResource.class)) {
            index.add(webResource.getSimpleName().toString());
            try {
                process(webResource);
            } catch (Error e) {
                getMessager().printMessage(Kind.ERROR, "can't process WebResource: " + toString(e), webResource);
                throw e;
            } catch (RuntimeException e) {
                getMessager().printMessage(Kind.ERROR, "can't process WebResource: " + toString(e), webResource);
            }
        }
        note("end round");
        if (roundEnv.processingOver())
            note("found " + index);
        return false;
    }

    private synchronized void process(Element element) {
        note("process " + path(element));
        this.type = (TypeElement) element;
        String targetTypeName = type.getQualifiedName() + "WebResource";
        note("Generating " + targetTypeName);

        String source = generateSource();
        try {
            JavaFileObject sourceFile = createSourceFile(targetTypeName, type);
            try (Writer writer = sourceFile.openWriter()) {
                writer.write(source);
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
        return new WebResourceWriter(messager, type).run();
    }

    private String toString(Throwable e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}
