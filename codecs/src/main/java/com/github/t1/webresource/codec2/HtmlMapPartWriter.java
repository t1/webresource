package com.github.t1.webresource.codec2;

import java.util.Map;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import com.github.t1.webresource.accessors.MapAccessor;
import com.github.t1.webresource.html.*;

@RequiredArgsConstructor
public class HtmlMapPartWriter implements HtmlPartWriter {
    @Inject
    private HtmlPartResover parts;
    @Inject
    private MapAccessor accessor;

    private final Map<Object, Object> map;

    @Override
    public void writeTo(Part container) {
        try (Table table = container.table()) {
            printHeader(table);
            printBody(table);
        }
    }

    private void printHeader(Table table) {
        try (TR tr = table.tr()) {
            try (TD td = tr.td()) {
                td.write(accessor.keyTitle(map));
            }
            try (TD td = tr.td()) {
                td.write(accessor.valueTitle(map));
            }
        }
    }

    private void printBody(Table table) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            try (TR tr = table.tr()) {
                try (TD td = tr.td()) {
                    parts.of(entry.getKey()).writeTo(td);
                }
                try (TD td = tr.td()) {
                    parts.of(entry.getValue()).writeTo(td);
                }
            }
        }
    }
}
