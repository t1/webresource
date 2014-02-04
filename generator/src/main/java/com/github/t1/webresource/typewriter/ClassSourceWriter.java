package com.github.t1.webresource.typewriter;

import java.util.List;

import com.github.t1.webresource.WebResourceType;

public class ClassSourceWriter {

    private final ClassBuilder builder;
    private final WebResourceType type;
    private IndentedWriter out;

    public ClassSourceWriter(ClassBuilder builder, WebResourceType type) {
        this.builder = builder;
        this.type = type;
    }

    synchronized public String write() {
        this.out = new IndentedWriter();

        out.println("package " + builder.pkg + ";");
        out.println();

        imports(type);
        out.println();

        annotations();
        out.println("public class " + builder.type + " {");
        ++out.indent;

        fields();
        methods();

        --out.indent;
        out.println("}");

        return out.toString();
    }

    private void imports(WebResourceType type) {
        if (type.id != null)
            imports(type.id.imports);
        if (requiresKeyTypeImport(type))
            imports(type.key.imports);
        out.println("import java.util.*;");
        out.println();
        builderImports(builder.annotations);
        out.println("import javax.persistence.*;");
        out.println("import javax.persistence.criteria.*;");
        out.println("import javax.ws.rs.*;");
        out.println("import javax.ws.rs.core.*;");
        out.println("import javax.ws.rs.core.Response.Status;");
        if (type.version != null)
            out.println("import javax.ws.rs.core.Response.ResponseBuilder;");
        out.println();
        builderImports(builder.fields);
    }

    private void builderImports(List<? extends Builder> builder) {
        for (Builder annotation : builder) {
            for (Class<?> type : annotation.types()) {
                out.println("import " + type.getName() + ";");
            }
        }
    }

    private void imports(List<String> imports) {
        for (String imp : imports) {
            out.println("import " + imp + ";");
        }
    }

    private boolean requiresKeyTypeImport(WebResourceType type) {
        return type.key != null && !type.key.imports.isEmpty() && !type.key.imports.equals(type.id.imports);
    }

    private void annotations() {
        for (AnnotationBuilder annotation : builder.annotations) {
            out.println(annotation(annotation));
        }
    }

    private String annotation(AnnotationBuilder annotation) {
        StringBuilder line = new StringBuilder();
        line.append("@").append(annotation.type.getSimpleName());
        boolean first = true;
        if (!annotation.parameters.isEmpty()) {
            line.append("(");
            for (String name : annotation.parameters.keySet()) {
                if (first)
                    first = false;
                else
                    line.append(", ");
                if (!onlyValueParameter(annotation))
                    line.append(name).append(" = ");
                Object value = annotation.parameters.get(name);
                if (!isPrimitive(value))
                    line.append("\"");
                if (value instanceof Enum)
                    line.append(value.getClass().getSimpleName()).append('.');
                line.append(value);
                if (!isPrimitive(value))
                    line.append("\"");
            }
            line.append(")");
        }
        return line.toString();
    }

    private boolean isPrimitive(Object value) {
        return value instanceof Integer || value instanceof Boolean || value instanceof Enum<?>;
    }

    private boolean onlyValueParameter(AnnotationBuilder annotation) {
        return annotation.parameters.size() == 1 && "value".equals(annotation.parameters.keySet().iterator().next());
    }

    private void fields() {
        for (FieldBuilder field : builder.fields) {
            for (AnnotationBuilder annotation : field.annotations)
                out.println(annotation(annotation));
            out.println(field(field));
            out.println();
        }
    }

    private Object field(FieldBuilder field) {
        StringBuilder line = new StringBuilder();
        line.append(field.visibility).append(" ");
        if (field.final_)
            line.append("final ");
        line.append(field.type.getSimpleName()).append(" ").append(field.name);
        line.append(field.initialization);
        line.append(";");
        return line.toString();
    }

    private void methods() {
        boolean first = true;
        for (MethodBuilder method : builder.methods) {
            if (first)
                first = false;
            else
                out.println();
            for (AnnotationBuilder annotation : method.annotations)
                out.println(annotation(annotation));
            out.println(methodDeclaration(method));
            out.indent++;
            out.printIndented(method.body);
            out.indent--;
            out.println("}");
        }
    }

    private Object methodDeclaration(MethodBuilder method) {
        StringBuilder line = new StringBuilder();
        line.append(method.visibility).append(" ");
        line.append(method.returnType.getSimpleName()).append(" ").append(method.name).append("(");
        boolean first = true;
        for (ParameterBuilder parameter : method.parameters) {
            if (first)
                first = false;
            else
                line.append(", ");
            for (AnnotationBuilder annotation : parameter.annotations) {
                line.append(annotation(annotation)).append(' ');
            }
            line.append(parameter.type.getSimpleName());
            if (parameter.uncollectedType != null)
                line.append('<').append(parameter.uncollectedType.getSimpleName()).append('>');
            line.append(' ').append(parameter.name);
        }
        line.append(") {");
        return line.toString();
    }
}
