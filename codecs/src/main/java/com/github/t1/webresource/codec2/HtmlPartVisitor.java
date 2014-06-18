package com.github.t1.webresource.codec2;

import java.net.URI;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.github.t1.webresource.html.Part;
import com.github.t1.webresource.meta2.*;

@RequestScoped
public class HtmlPartVisitor {
    @Inject
    private SequenceHtmlPartWriter sequenceWriter;
    @Inject
    private CompoundHtmlPartWriter compoundWriter;
    @Inject
    private UriHtmlPartWriter uriWriter;
    @Inject
    private PrimitiveHtmlPartWriter primitiveWriter;

    public void visit(Item item, final Part container) {
        ItemVisitor visitor = new ItemVisitor() {
            @Override
            public void visit(Primitive<?> primitive) {
                HtmlPartWriter<Primitive<?>> writer = primitiveWriterFor(primitive.getObject());
                writer.write(primitive, container);
            }

            @SuppressWarnings({ "unchecked", "rawtypes" })
            private HtmlPartWriter<Primitive<?>> primitiveWriterFor(Object item) {
                if (item instanceof URI) {
                    return (HtmlPartWriter) uriWriter;
                } else {
                    return (HtmlPartWriter) primitiveWriter;
                }
            }

            @Override
            public void visit(Sequence sequence) {
                sequenceWriter.write(sequence, container);
            }

            @Override
            public void visit(Compound compound) {
                compoundWriter.write(compound, container);
            }
        };
        item.visit(visitor);
    }
}
