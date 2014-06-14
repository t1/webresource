package com.github.t1.webresource.codec2;

import java.util.List;

import javax.inject.Inject;

import com.github.t1.webresource.html.*;

public class HtmlListPartWriter implements HtmlPartWriter<List<?>> {
    @Inject
    private HtmlPartResover parts;

    @Override
    public void write(List<?> list, Part container) {
        try (UL ul = container.ul()) {
            for (Object item : list) {
                try (LI li = ul.li()) {
                    parts.of(item).write(item, li);
                }
            }
        }
    }
}
