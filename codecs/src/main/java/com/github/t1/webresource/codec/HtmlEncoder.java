package com.github.t1.webresource.codec;

import static com.github.t1.webresource.meta.SimpleTrait.*;

import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.util.*;

import lombok.Data;

import com.github.t1.webresource.meta.*;

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

    private final Item item;
    private final Writer escaped;
    private final Writer unescaped;
    private final Map<String, Integer> ids = new HashMap<>();
    private final URI baseUri;

    public HtmlEncoder(Object t, Writer out, URI baseUri) {
        this.item = Items.newItem(t);
        this.escaped = new HtmlEscapeWriter(out);
        this.unescaped = out;
        this.baseUri = baseUri;
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
        } catch (Exception e) {
            unescaped.write("<!-- ............................................................\n");
            e.printStackTrace(new PrintWriter(unescaped));
            unescaped.write("............................................................ -->\n");
            throw e;
        }
    }

    private void nl() throws IOException {
        unescaped.append('\n');
    }

    private void writeHead() throws IOException {
        if (!item.isSimple()) {
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
        for (Trait trait : item.traits()) {
            if (trait.is(HtmlHead.class)) {
                delim.write();
                titleString.append(Objects.toString(item.get(trait)));
            }
        }
        return titleString.toString();
    }

    private void writeStyleSheets() throws IOException {
        if (item.is(HtmlStyleSheet.class)) {
            nl();
            writeStyleSheet(item.get(HtmlStyleSheet.class));
        }
        if (item.is(HtmlStyleSheets.class)) {
            nl();
            for (HtmlStyleSheet styleSheet : item.get(HtmlStyleSheets.class).value()) {
                writeStyleSheet(styleSheet);
            }
        }
    }

    private void writeStyleSheet(HtmlStyleSheet styleSheet) throws IOException {
        URI uri = URI.create(styleSheet.value());
        if (styleSheet.inline()) {
            try (Tag style = new Tag("style")) {
                nl();
                writeResource(uri);
            }
        } else {
            if (!isRootPath(uri))
                uri = insertApplicationPath(uri);
            unescaped.write("<link rel='stylesheet' href='" + uri + "' type='text/css'/>\n");
        }
    }

    private void writeResource(URI uri) throws IOException {
        if (!uri.isAbsolute()) {
            if (uri.getPath() == null)
                throw new IllegalArgumentException("the given uri has no path: " + uri);
            if (uri.getPath().startsWith("/")) {
                uri = baseUri.resolve(uri.getPath());
            } else {
                Path path = Paths.get(baseUri.getPath()).subpath(0, 1).resolve(uri.getPath());
                uri = baseUri.resolve("/" + path);
            }
        }
        try (InputStream inputStream = uri.toURL().openStream()) {
            write(inputStream);
        }
    }

    private void write(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        while (true) {
            String line = reader.readLine();
            if (line == null)
                break;
            unescaped.write(line);
            nl();
        }
    }

    private boolean isRootPath(URI uri) {
        return uri.isAbsolute() || uri.getPath() == null || uri.getPath().startsWith("/");
    }

    private URI insertApplicationPath(URI uri) {
        return uri.resolve("/" + applicationPath().resolve(uri.getPath()));
    }

    /**
     * The path of the JAX-RS base-uri starts with the resource base (often 'rest'), but we need the application base,
     * which is the first path element.
     */
    private Path applicationPath() {
        return Paths.get(baseUri.getPath()).getName(0);
    }

    private void writeBody() throws IOException {
        if (item.isNull())
            return;
        nl();
        if (item.isList()) {
            writeItemList();
        } else {
            writeMap();
        }
    }

    private void writeItemList() throws IOException {
        List<Item> list = item.getList();
        if (list.isEmpty())
            return;
        List<Trait> traits = list.get(0).traits();
        switch (traits.size()) {
            case 0:
                break;
            case 1:
                writeTraitList(list, traits.get(0));
                break;
            default:
                writeTable(list, traits);
        }
    }

    private void writeTraitList(List<Item> list, Trait trait) throws IOException {
        try (Tag ul = new Tag("ul")) {
            for (Item element : list) {
                try (Tag li = new Tag("li")) {
                    writeField(element, trait, null);
                }
            }
        }
    }

    private void writeTable(List<Item> list, List<Trait> traits) throws IOException {
        try (Tag table = new Tag("table")) {
            try (Tag thead = new Tag("thead")) {
                writeTableHead(traits);
            }
            try (Tag tbody = new Tag("tbody")) {
                for (Item element : list) {
                    writeTableRow(element, traits);
                }
            }
        }
    }

    private void writeTableHead(List<Trait> traits) throws IOException {
        try (Tag tr = new Tag("tr")) {
            for (Trait trait : traits) {
                try (Tag th = new Tag("th")) {
                    escaped.append(trait.getName());
                }
            }
        }
    }

    // this duplicates a lot of writeTableHead... closures would be nice, here ;-)
    private void writeTableRow(Item rowItem, List<Trait> traits) throws IOException {
        try (Tag tr = new Tag("tr")) {
            for (Trait trait : traits) {
                try (Tag td = new Tag("td")) {
                    writeField(rowItem, trait, null);
                }
            }
        }
    }

    private void writeField(Item item, Trait trait, String id) throws IOException {
        Object value = item.get(trait);
        if (value == null) {
            // append nothing
        } else if (item.isSimple()) {
            escaped.append(Objects.toString(value));
        } else {
            Item cellItem = Items.newItem(value);
            if (cellItem.isList()) {
                writeTraitList(cellItem.getList(), SIMPLE);
            } else {
                HtmlField field = new HtmlField(item, trait).id(id);
                unescaped.append(field);
            }
        }
    }

    private void writeMap() throws IOException {
        List<Trait> traits = item.traits();
        switch (traits.size()) {
            case 0:
                break;
            case 1:
                writeField(item, traits.get(0), null);
                break;
            default:
                writeTraits(traits);
        }
    }

    private void writeTraits(List<Trait> traits) throws IOException {
        for (Trait trait : traits) {
            try (Tag div = new Tag("div")) {
                String name = trait.getName();
                String id = id(name);
                try (Tag label = new Tag("label", new Attribute("for", id), new Attribute("class", name + "-label"))) {
                    escaped.write(name);
                }
                writeField(item, trait, id);
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
