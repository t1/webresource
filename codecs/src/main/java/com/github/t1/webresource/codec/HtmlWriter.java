package com.github.t1.webresource.codec;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.github.t1.webresource.codec.HtmlOut.Tag;
import com.github.t1.webresource.meta.Item;

/** A helper class to write objects as an html string... without the actual binding to JAX-RS, etc. */
public class HtmlWriter {
    @Inject
    HtmlOut out;
    @Inject
    HtmlHeadWriter htmlHeadWriter;
    @Inject
    HtmlBodyWriter htmlBodyWriter;
    @Inject
    Instance<HtmlDecorator> decorators;

    public void write(Item item) {
        try (Tag html = out.tag("html")) {
            out.nl();
            try (Tag head = out.tag("head")) {
                htmlHeadWriter.write(item);
            } catch (Exception e) {
                out.write("error writing head");
                throw e;
            }
            try (Tag body = out.tag("body")) {
                htmlBodyWriter.write(item);
                for (HtmlDecorator decorator : decorators) {
                    decorator.decorate(item);
                }
            } catch (Exception e) {
                out.write("error writing body");
                throw e;
            }
        } catch (Exception e) {
            out.write("<!-- ............................................................\n");
            out.write(e);
            out.write("............................................................ -->\n");
            throw e;
        }
    }
}
