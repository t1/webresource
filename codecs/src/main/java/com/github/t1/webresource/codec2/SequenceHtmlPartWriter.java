package com.github.t1.webresource.codec2;

import java.util.Iterator;

import javax.inject.Inject;

import com.github.t1.webresource.html.*;
import com.github.t1.webresource.meta2.*;

public class SequenceHtmlPartWriter implements HtmlPartWriter<Sequence> {
    @Inject
    private HtmlPartVisitor parts;

    @Override
    public void write(Sequence sequence, Part container) {
        Compound contained = compoundIn(sequence);
        if (contained == null) {
            writeList(sequence, container);
        } else {
            writeTable(contained, sequence, container);
        }
    }

    private Compound compoundIn(Sequence sequence) {
        Iterator<Item> iterator = sequence.iterator();
        if (!iterator.hasNext())
            return null;
        Item item = iterator.next();
        if (item instanceof Compound)
            return (Compound) item;
        return null;
    }

    private void writeList(Sequence sequence, Part container) {
        try (UL ul = container.ul()) {
            for (Item item : sequence) {
                try (LI li = ul.li()) {
                    parts.visit(item, li);
                }
            }
        }
    }

    private void writeTable(Compound contained, Sequence sequence, Part container) {
        try (Table table = container.table()) {
            printHeader(contained, table);
            printBody(sequence, table);
        }
    }

    private void printHeader(Compound contained, Table table) {
        try (TR tr = table.tr()) {
            for (Compound.Property property : contained.properties()) {
                try (TD td = tr.td()) {
                    td.write(property.name().toString());
                }
            }
        }
    }

    private void printBody(Sequence sequence, Table table) {
        for (Item item : sequence) {
            Compound compound = (Compound) item;
            try (TR tr = table.tr()) {
                for (Compound.Property property : compound.properties()) {
                    try (TD td = tr.td()) {
                        Item value = property.value();
                        parts.visit(value, td);
                    }
                }
            }
        }
    }
}
