package com.github.t1.webresource.codec2;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import com.github.t1.webresource.html.*;
import com.github.t1.webresource.meta2.*;

@RequiredArgsConstructor
public class CompoundHtmlPartWriter implements HtmlPartWriter<Compound> {
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
        if (compound.keyTitle() == null || compound.valueTitle() == null)
            return;
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
        for (Compound.Property entry : compound.properties()) {
            try (TR tr = table.tr()) {
                try (TD td = tr.td()) {
                    Item name = entry.name();
                    parts.visit(name, td);
                }
                try (TD td = tr.td()) {
                    Item value = entry.value();
                    parts.visit(value, td);
                }
            }
        }
    }
}
