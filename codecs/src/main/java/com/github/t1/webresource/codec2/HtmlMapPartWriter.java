package com.github.t1.webresource.codec2;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import com.github.t1.webresource.html.*;
import com.github.t1.webresource.meta2.*;

@RequiredArgsConstructor
public class HtmlMapPartWriter implements HtmlPartWriter<Compound> {
    @Inject
    private HtmlPartVisitor parts;

    @Override
    public void write(Compound compound, Part container) {
        try (Table table = container.table()) {
            printHeader(compound, table);
            printBody(compound, table);
        }
    }

    private void printHeader(Compound compound, Table table) {
        try (TR tr = table.tr()) {
            try (TD td = tr.td()) {
                td.write(compound.keyTitle().toString());
            }
            try (TD td = tr.td()) {
                td.write(compound.valueTitle().toString());
            }
        }
    }

    private void printBody(Compound compound, Table table) {
        for (Compound.Entry entry : compound) {
            try (TR tr = table.tr()) {
                try (TD td = tr.td()) {
                    Item key = entry.getKey();
                    parts.visit(key, td);
                }
                try (TD td = tr.td()) {
                    Item value = entry.getValue();
                    parts.visit(value, td);
                }
            }
        }
    }
}
