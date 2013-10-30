package com.github.t1.webresource.codec;

import javax.inject.Inject;

import com.github.t1.webresource.meta.Item;

/** A helper class to write objects as an html string... without the actual binding to JAX-RS, etc. */
public class HtmlWriter extends AbstractHtmlWriter {
    @Inject
    HtmlHeadWriter htmlHeadWriter;
    @Inject
    HtmlBodyWriter htmlBodyWriter;

    public void write(Item item) {
        try (Tag html = new Tag("html")) {
            nl();
            try (Tag head = new Tag("head")) {
                htmlHeadWriter.write(item);
            } catch (Exception e) {
                write("error writing head");
                throw e;
            }
            try (Tag body = new Tag("body")) {
                htmlBodyWriter.write(item);
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
