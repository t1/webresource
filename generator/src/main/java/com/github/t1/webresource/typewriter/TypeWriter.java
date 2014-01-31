package com.github.t1.webresource.typewriter;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import com.github.t1.webresource.WebResourceType;

public class TypeWriter {

    private final String pkg;
    private final String type;
    private final List<AnnotationBuilder> annotations = new ArrayList<>();
    private final List<FieldBuilder> fields = new ArrayList<>();

    public TypeWriter(String pkg, String type) {
        this.pkg = pkg;
        this.type = type;
    }

    public AnnotationBuilder annotate(Class<? extends Annotation> type) {
        AnnotationBuilder builder = new AnnotationBuilder(type);
        annotations.add(builder);
        return builder;
    }

    public FieldBuilder field(Class<?> type, String string) {
        FieldBuilder builder = new FieldBuilder(type, string);
        fields.add(builder);
        return builder;
    }

    public void writeTo(IndentedWriter out, WebResourceType type) {
        out.append("package " + pkg + ";");
        out.nl();

        imports(out, type);

        out.nl();
        for (AnnotationBuilder annotation : annotations) {
            out.append(annotation);
        }
        out.append("public class " + this.type + " {");
        ++out.indent;

        for (FieldBuilder field : fields) {
            out.append(field.toString());
        }
    }

    private void imports(IndentedWriter out, WebResourceType type) {
        if (type.id != null)
            imports(type.id.imports, out);
        if (requiresKeyTypeImport(type))
            imports(type.key.imports, out);
        out.append("import java.util.*;");
        out.nl();
        imports(out, annotations);
        out.append("import javax.persistence.*;");
        out.append("import javax.persistence.criteria.*;");
        out.append("import javax.ws.rs.*;");
        out.append("import javax.ws.rs.core.*;");
        out.append("import javax.ws.rs.core.Response.Status;");
        if (type.version != null)
            out.append("import javax.ws.rs.core.Response.ResponseBuilder;");
        out.nl();
        imports(out, fields);
    }

    private void imports(IndentedWriter out, List<? extends Builder> builder) {
        for (Builder annotation : builder) {
            for (Class<?> type : annotation.types()) {
                out.append("import " + type.getName() + ";");
            }
        }
    }

    private void imports(List<String> imports, IndentedWriter out) {
        for (String imp : imports) {
            out.append("import " + imp + ";");
        }
    }

    private boolean requiresKeyTypeImport(WebResourceType type) {
        return type.key != null && !type.key.imports.isEmpty() && !type.key.imports.equals(type.id.imports);
    }
}
