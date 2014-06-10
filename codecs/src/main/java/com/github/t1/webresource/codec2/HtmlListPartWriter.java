package com.github.t1.webresource.codec2;

import java.util.List;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import com.github.t1.webresource.html.*;

@RequiredArgsConstructor
public class HtmlListPartWriter implements HtmlPartWriter {
    @Inject
    private HtmlPartResover parts;

    private final List<?> list;

    @Override
    public void writeTo(Part container) {
        try (UL ul = container.ul()) {
            for (Object item : list) {
                try (LI li = ul.li()) {
                    parts.of(item).writeTo(li);
                }
            }
        }
    }
}
