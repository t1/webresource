package com.github.t1.webresource;

import java.io.*;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

class WebResourceGenerator extends AbstractGenerator {
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final Pattern VAR = Pattern.compile("\\$\\{([^}]+)\\}");

    private TypeElement type;

    public WebResourceGenerator(Messager messager, Filer filer, Elements utils) {
        super(messager, filer, utils);
    }

    private String replaceVariable(String variable) {
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
        StringBuffer result = new StringBuffer();
        BufferedReader reader = null;
        try {
            try {
                reader = new BufferedReader(getTemplateReader());
                String line;
                while ((line = reader.readLine()) != null) {
                    appendLine(result, line);
                }
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result.toString();
    }

    /**
     * The {@link Reader} for your template. Instead of overriding this method, you can also pass the name of a template
     * resource relative to your subclass into the {@link #TemplateGenerator(Messager, Filer, String)} constructor.
     */
    private Reader getTemplateReader() {
        String templateName = "/WebResource.template";
        InputStream intputStream = this.getClass().getResourceAsStream(templateName);
        if (intputStream == null)
            throw new RuntimeException("can't find " + templateName);
        return new InputStreamReader(intputStream, UTF_8);
    }

    private void appendLine(StringBuffer result, String line) {
        Matcher matcher = VAR.matcher(line);
        while (matcher.find()) {
            matcher.appendReplacement(result, replaceVariable(matcher.group(1)));
        }
        matcher.appendTail(result).append('\n');
    }
}
