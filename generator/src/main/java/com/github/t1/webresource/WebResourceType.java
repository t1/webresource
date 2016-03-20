package com.github.t1.webresource;

import com.github.t1.webresource.annotations.*;
import com.github.t1.webresource.tools.StringTool;
import com.github.t1.webresource.typewriter.TypeString;

import javax.lang.model.element.*;
import java.util.*;

public class WebResourceType {
    private final TypeElement typeElement;
    final TypeString type;
    final String pkg;
    final String simple;
    final String entityName;
    final String lower;
    final String plural;
    final String qualified;
    final boolean extended;
    public final WebResourceField id;
    public final WebResourceField key;
    public final WebResourceField version;
    final List<WebResourceField> subResourceFields;

    WebResourceType(TypeElement typeElement) {
        this.typeElement = typeElement;
        this.pkg = pkg();
        this.type = new TypeString(typeElement.getQualifiedName().toString());
        this.simple = typeElement.getSimpleName().toString();
        this.entityName = entity();
        this.lower = simple.toLowerCase();
        this.plural = StringTool.of(StringTool::lowercase).and(StringTool::pluralize).apply(simple);
        this.qualified = qualified();
        this.extended = isExtended();
        this.id = id();
        this.key = key();
        this.version = version();
        this.subResourceFields = subResourceFields();
    }

    private String pkg() {
        for (Element element = typeElement; element != null; element = element.getEnclosingElement()) {
            if (ElementKind.PACKAGE == element.getKind()) {
                return ((QualifiedNameable) element).getQualifiedName().toString();
            }
        }
        throw new IllegalStateException("no package for " + typeElement);
    }

    private boolean isExtended() {
        WebResource annotation = typeElement.getAnnotation(WebResource.class);
        if (annotation == null)
            throw new RuntimeException("expected type to be annotated as WebResource: " + typeElement);
        return annotation.extended();
    }

    private String qualified() {
        return typeElement.getQualifiedName().toString();
    }

    private String entity() {
        AnnotationMirror annotation = WebResourceField.getAnnotation(typeElement, "javax.persistence.Entity");
        if (annotation != null)
            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry :
                    annotation.getElementValues().entrySet())
                if (entry.getKey().getSimpleName().contentEquals("name"))
                    return entry.getValue().getValue().toString();
        return typeElement.getSimpleName().toString();
    }

    private WebResourceField id() {
        // don't use the Id type itself, it may not be available at compile-time
        return WebResourceField.findField(typeElement, "javax.persistence.Id");
    }

    private WebResourceField key() {
        WebResourceField keyField = WebResourceField.findField(typeElement, WebResourceKey.class.getName());
        return (keyField == null) ? id : keyField;
    }

    private WebResourceField version() {
        // don't use the Version type itself, it may not be available at compile-time
        return WebResourceField.findField(typeElement, "javax.persistence.Version");
    }

    private List<WebResourceField> subResourceFields() {
        return WebResourceField.findFields(typeElement, WebSubResource.class.getName());
    }

    boolean primary() {
        return id.equals(key);
    }
}
