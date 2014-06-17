package com.github.t1.webresource.codec2;

import java.net.URI;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.github.t1.webresource.html.Part;
import com.github.t1.webresource.meta2.*;

@RequestScoped
public class HtmlPartVisitor {
    @Inject
    private HtmlListPartWriter listWriter;
    @Inject
    private HtmlMapPartWriter mapWriter;
    @Inject
    private HtmlLinkPartWriter uriWriter;
    @Inject
    private ToStringPartWriter toStringWriter;

    public void visit(Item item, final Part container) {
        ItemVisitor visitor = new ItemVisitor() {
            @Override
            public void visit(Primitive primitive) {
                HtmlPartWriter<Object> writer = primitiveWriterFor(primitive.getObject());
                writer.write(primitive.getObject(), container);
            }

            @SuppressWarnings({ "unchecked", "rawtypes" })
            private HtmlPartWriter<Object> primitiveWriterFor(Object item) {
                if (item instanceof URI) {
                    return (HtmlPartWriter) uriWriter;
                } else {
                    return toStringWriter;
                }
            }

            @Override
            public void visit(Sequence sequence) {
                listWriter.write(sequence, container);
            }

            @Override
            public void visit(Compound compound) {
                mapWriter.write(compound, container);
            }
        };
        item.visit(visitor);
    }
}
