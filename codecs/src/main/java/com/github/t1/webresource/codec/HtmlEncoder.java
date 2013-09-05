package com.github.t1.webresource.codec;

import java.io.*;
import java.net.URI;

import com.github.t1.webresource.meta.*;

/** A helper class to write objects as an html string... without the actual binding */
public class HtmlEncoder extends AbstractHtmlWriter {
    private final Item item;

    public HtmlEncoder(Object t, Writer out, URI baseUri) {
        super(new HtmlWriter(out, baseUri));
        this.item = Items.newItem(t);
    }

    public void write() throws IOException {
        try (Tag html = new Tag("html")) {
            nl();
            try (Tag head = new Tag("head")) {
                new HtmlHeadWriter(out, item).write();
            }
            try (Tag body = new Tag("body")) {
                new HtmlBodyWriter(out, item).write();
            }
        } catch (Exception e) {
            out.write("<!-- ............................................................\n");
            e.printStackTrace(new PrintWriter(out));
            out.write("............................................................ -->\n");
            throw e;
        }
    }
}
