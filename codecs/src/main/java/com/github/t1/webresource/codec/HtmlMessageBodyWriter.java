package com.github.t1.webresource.codec;

import com.github.t1.meta.Meta;
import com.github.t1.webresource.util.StringTool;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.URI;
import java.util.Collection;

import static com.github.t1.webresource.util.StringTool.*;
import static java.util.Arrays.*;
import static javax.ws.rs.core.MediaType.*;

@Slf4j
@Provider
@Produces("text/html")
public class HtmlMessageBodyWriter implements MessageBodyWriter<Object> {
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return TEXT_HTML_TYPE.isCompatible(mediaType);
    }

    @Override
    public long getSize(Object o, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(Object o, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException {
        log.debug("write {}/{} with {} as {} with headers {}", type, genericType, asList(annotations), mediaType,
                httpHeaders);
        Writer out = new OutputStreamWriter(entityStream);
        out.append("<!DOCTYPE html>\n"
                + "<html>\n"
                + "  <head>\n"
                + "    <meta charset=\"utf-8\"/>\n"
                + "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"/>\n"
                + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/>\n"
                + "\n");
        out.append("    <title>").append(title(genericType)).append("</title>\n");
        out.append("\n");
        out.append(bootstrapCss());
        out.append("  </head>\n"
                + "  <body class=\"container-fluid\" style=\"padding-top: 70px\">\n");

        appendPojo(out, o);

        out.append(jqueryJs());
        out.append(bootstrapJs());
        out.append("  </body>\n"
                + "</html>\n");

        out.flush();
    }

    private String bootstrapCss() {
        return "    <link rel=\"stylesheet\" href=\"" + bootstrapCssUri() + "\" \n"
                + "        integrity=\"" + bootstrapCssIntegrity() + "\" \n"
                + "        crossorigin=\"anonymous\">\n";
    }

    public URI bootstrapCssUri() {
        return URI.create("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css");
    }

    @SuppressWarnings("SpellCheckingInspection")
    public String bootstrapCssIntegrity() {
        return "sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7";
    }

    private String bootstrapJs() {
        return "    <script src=\"" + bootstrapJsUri() + "\" \n"
                + "        integrity=\"" + bootstrapJsIntegrity() + "\" \n"
                + "        crossorigin=\"anonymous\"></script>\n";
    }

    public URI bootstrapJsUri() {
        return URI.create("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js");
    }

    @SuppressWarnings("SpellCheckingInspection")
    public String bootstrapJsIntegrity() {
        return "sha384-0mSbJDEHialfmuBBQP6A4Qrprq5OVfW37PRR3j5ELqxss1yVqOtnepnHVP9aJ7xS";
    }

    private String jqueryJs() {
        return "    <script src=\"" + jqueryUri() + "\" \n"
                + "        integrity=\"" + jqueryIntegrity() + "\" \n"
                + "        crossorigin=\"anonymous\"></script>\n";
    }

    public URI jqueryUri() {
        return URI.create("https://code.jquery.com/jquery-2.2.1.min.js");
    }

    @SuppressWarnings("SpellCheckingInspection")
    public String jqueryIntegrity() {
        return "sha384-8C+3bW/ArbXinsJduAjm9O7WNnuOcO+Bok/VScRYikawtvz4ZPrpXtGfKIewM9dK";
    }

    private String title(Type type) {
        StringTool tool = of(camelToWords());
        if (isCollection(type)) {
            type = elementType(type);
            tool = tool.and(StringTool::pluralize);
        }
        String typeName = (type instanceof Class)
                ? ((Class<?>) type).getSimpleName()
                : type.getTypeName();
        return tool.apply(typeName);
    }

    private boolean isCollection(Type type) {
        return type instanceof ParameterizedType && Collection.class.isAssignableFrom(raw(type));
    }

    private Class<?> raw(Type type) {
        return (Class<?>) ((ParameterizedType) type).getRawType();
    }

    private Type elementType(Type type) {
        return ((ParameterizedType) type).getActualTypeArguments()[0];
    }

    private void appendPojo(Writer out, Object pojo) {
        new Meta().visitTo(pojo).by(new HtmlBodyVisitor(out)).run();
    }
}
