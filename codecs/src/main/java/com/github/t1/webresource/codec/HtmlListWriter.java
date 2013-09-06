package com.github.t1.webresource.codec;

import java.io.*;
import java.net.URI;
import java.util.List;

import com.github.t1.webresource.meta.*;

public class HtmlListWriter extends AbstractHtmlWriter {

    private final List<Item> list;
    private final Trait trait;

    public HtmlListWriter(Writer out, URI baseUri, List<Item> list, Trait trait) {
        super(out, baseUri);
        this.list = list;
        this.trait = trait;
    }

    public void write() throws IOException {
        try (Tag ul = new Tag("ul")) {
            for (Item element : list) {
                try (Tag li = new Tag("li")) {
                    writeField(element, trait, null);
                }
            }
        }
    }
}
