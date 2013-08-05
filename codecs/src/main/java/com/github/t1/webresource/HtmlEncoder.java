package com.github.t1.webresource;

import java.io.*;
import java.net.URI;
import java.nio.file.*;
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

    private final Writer escaped;
    private final Writer unescaped;
    private final Path applicationPath;
    private final Map<String, Integer> ids = new HashMap<>();

    public HtmlEncoder(Writer out, URI baseUri) {
        this.unescaped = out;
        this.applicationPath = applicationPath(baseUri);
        this.escaped = new HtmlEscapeWriter(out);
    }

    /**
     * The path of the JAX-RS base-uri starts with the resource base (often 'rest'), but we need the application base,
     * which is the first path element.
     */
    private Path applicationPath(URI baseUri) {
        return Paths.get(baseUri.getPath()).getName(0);
    }

    public void write(Object object) throws IOException {
        try (Tag html = new Tag("html")) {
            nl();
            Holder holder = new Holder(object);
            try (Tag head = new Tag("head")) {
                writeHead(holder);
            }
            try (Tag body = new Tag("body")) {
                writeBody(holder);
            }
        }
    }

    private void nl() throws IOException {
        unescaped.append('\n');
    }

    private void writeHead(Holder holder) throws IOException {
        if (!holder.isSimple()) {
            writeTitle(holder);
            writeStyleSheets(holder);
        }
    }

    private void writeTitle(Holder holder) throws IOException {
        String titleString = titleString(holder);
        if (!titleString.isEmpty()) {
            try (Tag title = new Tag("title")) {
                escaped.append(titleString);
            }
        }
    }

    private String titleString(Holder holder) throws IOException {
        StringWriter titleString = new StringWriter();
        Delimiter delim = new Delimiter(titleString, " - ");
        for (Property property : holder.properties()) {
            if (property.is(HtmlHead.class)) {
                delim.write();
                titleString.append(holder.get(property));
            }
        }
        return titleString.toString();
    }

    private void writeStyleSheets(Holder holder) throws IOException {
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

    private void writeBody(Holder holder) throws IOException {
        if (holder.isNull())
            return;
        nl();
        if (holder.isList()) {
            writeList(holder);
        } else {
            writeHolder(holder);
        }
    }

    private void writeList(Holder holder) throws IOException {
        List<Holder> list = holder.getList();
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

    private void writeBulletList(List<Holder> list, Property property) throws IOException {
        try (Tag ul = new Tag("ul")) {
            for (Holder element : list) {
                try (Tag li = new Tag("li")) {
                    escaped.append(element.get(property));
                }
            }
        }
    }

    private void writeTable(List<Holder> list, List<Property> properties) throws IOException {
        try (Tag ul = new Tag("table")) {
            writeTableHead(properties);
            for (Holder element : list) {
                writeTableRow(element, properties);
            }
        }
    }

    private void writeTableHead(List<Property> properties) throws IOException {
        try (Tag tr = new Tag("tr")) {
            for (Property property : properties) {
                try (Tag td = new Tag("td")) {
                    escaped.append(property.getName());
                }
            }
        }
    }

    private void writeTableRow(Holder holder, List<Property> properties) throws IOException {
        try (Tag tr = new Tag("tr")) {
            for (Property property : properties) {
                try (Tag td = new Tag("td")) {
                    escaped.append(holder.get(property));
                }
            }
        }
    }

    private void writeHolder(Holder holder) throws IOException {
        List<Property> properties = holder.properties();
        switch (properties.size()) {
            case 0:
                break;
            case 1:
                if (holder.isSimple()) {
                    escaped.write(holder.get(Property.SIMPLE));
                } else {
                    writeBody(new Holder(holder.get(properties.get(0))));
                }
                break;
            default:
                writeProperties(holder, properties);
        }
    }

    private void writeProperties(Holder holder, List<Property> properties) throws IOException {
        for (Property property : properties) {
            try (Tag div = new Tag("div")) {
                String id = id(property.getName());
                try (Tag label = new Tag("label", new Attribute("for", id))) {
                    escaped.write(property.getName());
                }
                writeValue(id, holder.get(property));
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

    private void writeValue(String id, Object value) throws IOException {
        unescaped.append("<input id='" + id + "' type='text'");
        if (value != null)
            unescaped.append(" value='" + value + "' readonly");
        unescaped.append("/>\n");
    }
}
