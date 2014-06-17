package com.github.t1.webresource.codec2;

import javax.inject.Inject;

import com.github.t1.webresource.html.*;
import com.github.t1.webresource.meta2.*;

public class HtmlListPartWriter implements HtmlPartWriter<Sequence> {
    @Inject
    private HtmlPartVisitor parts;

    @Override
    public void write(Sequence sequence, Part container) {
        try (UL ul = container.ul()) {
            for (Item item : sequence) {
                try (LI li = ul.li()) {
                    parts.visit(item, li);
                }
            }
        }
    }
}
