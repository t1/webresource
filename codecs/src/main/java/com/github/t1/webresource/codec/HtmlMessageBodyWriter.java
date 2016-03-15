package com.github.t1.webresource.codec;

import com.github.t1.meta.Meta;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;

import static java.util.Arrays.*;
import static javax.ws.rs.core.MediaType.*;

@Slf4j
@Provider
@Produces("text/html")
public class HtmlMessageBodyWriter implements MessageBodyWriter<Object> {
    private static final String BOOTSTRAP_BASE = "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/";

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return TEXT_HTML_TYPE.isCompatible(mediaType);
    }

    @Override
    public long getSize(Object o, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(Object pojo, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException {
        log.debug("write {}/{} with {} as {} with headers {}", type, genericType, asList(annotations), mediaType,
                httpHeaders);

        new Builder(genericType, entityStream).build(pojo);
    }

    private class Builder {
        private final HtmlWriter html;
        private final Type genericType;

        public Builder(Type genericType, OutputStream entityStream) {
            this.genericType = genericType;
            this.html = new HtmlWriter(new OutputStreamWriter(entityStream));
        }

        public void build(Object pojo) {
            html.text("<!DOCTYPE html>").nl();
            html.open("html").nl();

            head();
            body(pojo);
            html.close("html").nl();

            html.flush();
        }

        private void head() {
            html.open("head").nl();
            html.open("meta").a("charset", "utf-8").close("meta").nl();
            html.open("meta").a("http-equiv", "X-UA-Compatible").a("content", "IE=edge").close("meta").nl();
            html.open("meta").a("name", "viewport").a("content", "width=device-width, initial-scale=1")
                    .close("meta").nl();
            html.nl();

            html.open("title").text(title()).close("title").nl();
            html.nl();
            bootstrapCss();
            html.close("head").nl();
        }

        private void body(Object pojo) {
            html.open("body").a("class", "container-fluid").a("style", "padding-top: 15px").nl();

            new Meta().visitTo(pojo).by(new HtmlBodyVisitor(genericType, html)).run();

            jqueryJs();
            bootstrapJs();
            html.close("body").nl();
        }

        private void bootstrapCss() {
            html.open("link")
                    .a("rel", "stylesheet")
                    .a("href", bootstrapCssUri())
                    .a("integrity", bootstrapCssIntegrity())
                    .a("crossorigin", "anonymous")
                    .close("link").nl();
        }

        private void bootstrapJs() {
            script(bootstrapJsUri(), bootstrapJsIntegrity());
        }

        private void jqueryJs() {
            script(jqueryUri(), jqueryIntegrity());
        }

        private void script(URI uri, String integrity) {
            html.open("script")
                    .a("src", uri)
                    .a("integrity", integrity)
                    .a("crossorigin", "anonymous")
                    .text("")
                    .close("script")
                    .nl();
        }

        private String title() {
            return new TitleBuilder(genericType).toString();
        }
    }


    public URI bootstrapCssUri() {
        return URI.create(BOOTSTRAP_BASE + "/css/bootstrap.min.css");
    }

    @SuppressWarnings("SpellCheckingInspection")
    public String bootstrapCssIntegrity() {
        return "sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7";
    }


    public URI bootstrapJsUri() {
        return URI.create(BOOTSTRAP_BASE + "/js/bootstrap.min.js");
    }

    @SuppressWarnings("SpellCheckingInspection")
    public String bootstrapJsIntegrity() {
        return "sha384-0mSbJDEHialfmuBBQP6A4Qrprq5OVfW37PRR3j5ELqxss1yVqOtnepnHVP9aJ7xS";
    }


    public URI jqueryUri() {
        return URI.create("https://code.jquery.com/jquery-2.2.1.min.js");
    }

    @SuppressWarnings("SpellCheckingInspection")
    public String jqueryIntegrity() {
        return "sha384-8C+3bW/ArbXinsJduAjm9O7WNnuOcO+Bok/VScRYikawtvz4ZPrpXtGfKIewM9dK";
    }
}
