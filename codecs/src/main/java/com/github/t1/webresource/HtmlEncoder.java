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
            PojoHolder pojo = new PojoHolder(object);
            try (Tag head = new Tag("head")) {
                writeHead(pojo);
            }
            try (Tag body = new Tag("body")) {
                writeBody(pojo);
            }
        }
    }

    private void nl() throws IOException {
        unescaped.append('\n');
    }

    private void writeHead(PojoHolder pojo) throws IOException {
        if (!pojo.isSimple()) {
            writeTitle(pojo);
            writeStyleSheets(pojo);
        }
    }

    private void writeTitle(PojoHolder pojo) throws IOException {
        String titleString = titleString(pojo);
        if (!titleString.isEmpty()) {
            try (Tag title = new Tag("title")) {
                escaped.append(titleString);
            }
        }
    }

    private String titleString(PojoHolder pojo) throws IOException {
        StringWriter titleString = new StringWriter();
        Delimiter delim = new Delimiter(titleString, " - ");
        for (PojoProperty property : pojo.properties()) {
            if (property.is(HtmlHead.class)) {
                delim.write();
                titleString.append(pojo.get(property));
            }
        }
        return titleString.toString();
    }

    private void writeStyleSheets(PojoHolder pojo) throws IOException {
        if (pojo.is(HtmlStyleSheet.class)) {
            nl();
            writeStyleSheet(pojo.get(HtmlStyleSheet.class));
        }
        if (pojo.is(HtmlStyleSheets.class)) {
            nl();
            for (HtmlStyleSheet styleSheet : pojo.get(HtmlStyleSheets.class).value()) {
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

    private void writeBody(PojoHolder pojo) throws IOException {
        if (pojo.isNull())
            return;
        nl();
        if (pojo.isList()) {
            writeList(pojo.getList());
        } else if (pojo.isSimple()) {
            escaped.write(pojo.get(PojoProperty.SIMPLE));
        } else {
            writePojo(pojo);
        }
    }

    private void writeList(List<?> list) throws IOException {
        if (list.isEmpty())
            return;
        PojoHolder pojo = new PojoHolder(list.get(0));
        if (pojo.isSimple()) {
            writeOnePropertyList(list, PojoProperty.SIMPLE);
        } else {
            List<PojoProperty> properties = pojo.properties();
            switch (properties.size()) {
                case 0:
                    break;
                case 1:
                    writeOnePropertyList(list, properties.get(0));
                    break;
                default:
                    writeTable(properties, list);
            }
        }
    }

    private void writeOnePropertyList(List<?> list, PojoProperty property) throws IOException {
        try (Tag ul = new Tag("ul")) {
            for (Object object : list) {
                try (Tag li = new Tag("li")) {
                    escaped.append(new PojoHolder(object).get(property));
                }
            }
        }
    }

    private void writeTable(List<PojoProperty> properties, List<?> list) throws IOException {
        try (Tag ul = new Tag("table")) {
            writeTableHead(properties);
            for (Object object : list) {
                writeTableRow(object, properties);
            }
        }
    }

    private void writeTableHead(List<PojoProperty> properties) throws IOException {
        try (Tag tr = new Tag("tr")) {
            for (PojoProperty property : properties) {
                try (Tag td = new Tag("td")) {
                    escaped.append(property.getName());
                }
            }
        }
    }

    private void writeTableRow(Object object, List<PojoProperty> properties) throws IOException {
        try (Tag tr = new Tag("tr")) {
            for (PojoProperty property : properties) {
                try (Tag td = new Tag("td")) {
                    escaped.append(new PojoHolder(object).get(property));
                }
            }
        }
    }

    private void writePojo(PojoHolder pojo) throws IOException {
        List<PojoProperty> properties = pojo.properties();
        switch (properties.size()) {
            case 0:
                break;
            case 1:
                writeBody(new PojoHolder(pojo.get(properties.get(0))));
                break;
            default:
                writeProperties(pojo, properties);
        }
    }

    private void writeProperties(PojoHolder pojo, List<PojoProperty> properties) throws IOException {
        for (PojoProperty property : properties) {
            try (Tag div = new Tag("div")) {
                String id = id(property.getName());
                try (Tag label = new Tag("label", new Attribute("for", id))) {
                    escaped.write(property.getName());
                }
                writeValue(id, pojo.get(property));
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
