package com.github.t1.webresource.codec2;

import java.util.Map;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import com.github.t1.webresource.html.*;
import com.github.t1.webresource.meta2.MapAccessor;

@RequiredArgsConstructor
public class HtmlMapPartWriter implements HtmlPartWriter<Map<Object, Object>> {
    @Inject
    private MapAccessor accessor;
    @Inject
    private HtmlPartResover parts;

    @Override
    public void write(Map<Object, Object> map, Part container) {
        try (Table table = container.table()) {
            printHeader(map, table);
            printBody(map, table);
        }
    }

    private void printHeader(Map<Object, Object> map, Table table) {
        try (TR tr = table.tr()) {
            try (TD td = tr.td()) {
                td.write(accessor.keyTitle(map));
            }
            try (TD td = tr.td()) {
                td.write(accessor.valueTitle(map));
            }
        }
    }

    private void printBody(Map<Object, Object> map, Table table) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            try (TR tr = table.tr()) {
                try (TD td = tr.td()) {
                    Object key = entry.getKey();
                    parts.of(key).write(key, td);
                }
                try (TD td = tr.td()) {
                    Object value = entry.getValue();
                    parts.of(value).write(value, td);
                }
            }
        }
    }
}
