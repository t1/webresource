package com.github.t1.webresource;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

import lombok.Data;

/** A helper class to write objects as an html string... without the actual binding */
public class HtmlEncoder {
    @Data
    private static class Attribute {
        private final String name;
        private final String value;
    }

    private class Tag implements AutoCloseable {
        private final String name;

        public Tag(String name, Attribute... attributes) throws IOException {
            this.name = name;
            unescaped.append('<').append(name);
            for (Attribute attribute : attributes)
                unescaped.append(' ').append(attribute.name).append("='").append(attribute.value).append('\'');
            unescaped.append(">");
        }

        @Override
        public void close() throws IOException {
            unescaped.append("</").append(name).append(">\n");
        }
    }

    private final Holder<?> holder;
    private final Writer escaped;
    private final Writer unescaped;
    private final Path applicationPath;
    private final Map<String, Integer> ids = new HashMap<>();

    public HtmlEncoder(Object t, Writer out, Path applicationPath) {
        this.holder = new Holder<>(t);
        this.escaped = new HtmlEscapeWriter(out);
        this.unescaped = out;
        this.applicationPath = applicationPath;
    }

    public void write() throws IOException {
        try (Tag html = new Tag("html")) {
            nl();
            try (Tag head = new Tag("head")) {
                writeHead();
            }
            try (Tag body = new Tag("body")) {
                writeBody();
            }
        }
    }

    private void nl() throws IOException {
        unescaped.append('\n');
    }

    private void writeHead() throws IOException {
        if (!holder.isSimple()) {
            writeTitle();
            writeStyleSheets();
        }
    }

    private void writeTitle() throws IOException {
        String titleString = titleString();
        if (!titleString.isEmpty()) {
            try (Tag title = new Tag("title")) {
                escaped.append(titleString);
            }
        }
    }

    private String titleString() throws IOException {
        StringWriter titleString = new StringWriter();
        Delimiter delim = new Delimiter(titleString, " - ");
        for (Property property : holder.properties()) {
            if (property.is(HtmlHead.class)) {
                delim.write();
                titleString.append(Objects.toString(holder.get(property)));
            }
        }
        return titleString.toString();
    }

    private void writeStyleSheets() throws IOException {
        if (holder.is(HtmlStyleSheet.class)) {
            nl();
            writeStyleSheet(holder.get(HtmlStyleSheet.class));
        }
        if (holder.is(HtmlStyleSheets.class)) {
            nl();
            for (HtmlStyleSheet styleSheet : holder.get(HtmlStyleSheets.class).value()) {
                writeStyleSheet(styleSheet);
            }
        }
    }

    private void writeStyleSheet(HtmlStyleSheet styleSheet) throws IOException {
        String url = styleSheet.value();
        if (!url.startsWith("/"))
            url = "/" + applicationPath.resolve(url);
        unescaped.write("<link rel='stylesheet' href='" + url + "' type='text/css'/>\n");
    }

    private void writeBody() throws IOException {
        if (holder.isNull())
            return;
        nl();
        if (holder.isList()) {
            writeList();
        } else {
            writeMap();
        }
    }

    private void writeList() throws IOException {
        List<Holder<?>> list = holder.getList();
        if (list.isEmpty())
            return;
        List<Property> properties = list.get(0).properties();
        switch (properties.size()) {
            case 0:
                break;
            case 1:
                writeBulletList(list, properties.get(0));
                break;
            default:
                writeTable(list, properties);
        }
    }

    private void writeBulletList(List<Holder<?>> list, Property property) throws IOException {
        try (Tag ul = new Tag("ul")) {
            for (Holder<?> element : list) {
                try (Tag li = new Tag("li")) {
                    escaped.append(new HtmlField(element, property));
                }
            }
        }
    }

    private void writeTable(List<Holder<?>> list, List<Property> properties) throws IOException {
        try (Tag table = new Tag("table")) {
            try (Tag thead = new Tag("thead")) {
                writeTableHead(properties);
            }
            try (Tag tbody = new Tag("tbody")) {
                for (Holder<?> element : list) {
                    writeTableRow(element, properties);
                }
            }
        }
    }

    private void writeTableHead(List<Property> properties) throws IOException {
        try (Tag tr = new Tag("tr")) {
            for (Property property : properties) {
                try (Tag th = new Tag("th")) {
                    escaped.append(property.getName());
                }
            }
        }
    }

    // this duplicates a lot of writeTableHead... closures would be nice, here ;-)
    private void writeTableRow(Holder<?> element, List<Property> properties) throws IOException {
        try (Tag tr = new Tag("tr")) {
            for (Property property : properties) {
                try (Tag td = new Tag("td")) {
                    escaped.append(new HtmlField(element, property));
                }
            }
        }
    }

    private void writeMap() throws IOException {
        List<Property> properties = holder.properties();
        switch (properties.size()) {
            case 0:
                break;
            case 1:
                writeProperty(properties.get(0));
                break;
            default:
                writeProperties(properties);
        }
    }

    private void writeProperty(Property property) throws IOException {
        if (holder.isSimple()) { // prevent recursion
            escaped.append(new HtmlField(holder, Holder.SIMPLE));
        } else {
            Object value = holder.get(property);
            new HtmlEncoder(value, unescaped, applicationPath).writeBody();
        }
    }

    private void writeProperties(List<Property> properties) throws IOException {
        for (Property property : properties) {
            try (Tag div = new Tag("div")) {
                String id = id(property.getName());
                try (Tag label = new Tag("label", new Attribute("for", id))) {
                    escaped.write(property.getName());
                }
                Object value = holder.get(property);
                if (value instanceof List) {
                    writeBulletList((List<?>) value);
                } else {
                    unescaped.append(new HtmlField(holder, property).id(id));
                }
            }
        }
    }

    private void writeBulletList(List<?> list) throws IOException {
        try (Tag ul = new Tag("ul")) {
            for (Object element : list) {
                try (Tag li = new Tag("li")) {
                    escaped.append(Objects.toString(element));
                }
            }
        }
    }

    private String id(String name) {
        Integer i = ids.get(name);
        if (i == null)
            i = 0;
        ids.put(name, i + 1);
        return name + "-" + i;
    }
}
