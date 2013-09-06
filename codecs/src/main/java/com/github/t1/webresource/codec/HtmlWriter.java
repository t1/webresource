package com.github.t1.webresource.codec;

import java.io.*;
import java.net.URI;

import com.github.t1.webresource.meta.*;

/** A helper class to write objects as an html string... without the actual binding */
public class HtmlWriter extends AbstractHtmlWriter {
    private final Item item;

    public HtmlWriter(Object t, Writer out, URI baseUri) {
        super(out, baseUri);
        this.item = Items.newItem(t);
    }

    public void write() throws IOException {
        try (Tag html = new Tag("html")) {
            nl();
            try (Tag head = new Tag("head")) {
                writeHead(item);
            }
            try (Tag body = new Tag("body")) {
                writeBody(item);
            } catch (Exception e) {
                write("error writing body");
                throw e;
            }
        } catch (Exception e) {
            write("<!-- ............................................................\n");
            write(e);
            write("............................................................ -->\n");
            throw e;
        }
    }
}
