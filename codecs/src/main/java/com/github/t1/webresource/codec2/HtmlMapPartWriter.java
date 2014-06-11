package com.github.t1.webresource.codec2;

import java.util.Map;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import com.github.t1.webresource.accessors.*;
import com.github.t1.webresource.html.*;

@RequiredArgsConstructor
public class HtmlMapPartWriter implements HtmlPartWriter {
    @Inject
    private HtmlPartResover parts;
    @Inject
    private MetaDataStore metaData;

    private final Map<Object, Object> map;

    @Override
    public void writeTo(Part container) {
        try (Table table = container.table()) {
            MapMetaData meta = metaData.get(map);
            printHeader(meta, table);
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

    private void printHeader(MapMetaData meta, Table table) {
        String keyTitle = (meta == null) ? "Key" : meta.keyTitle();
        String valueTitle = (meta == null) ? "Value" : meta.valueTitle();
        try (TR tr = table.tr()) {
            try (TD td = tr.td()) {
                td.write(keyTitle);
            }
            try (TD td = tr.td()) {
                td.write(valueTitle);
            }
        }
    }
}
