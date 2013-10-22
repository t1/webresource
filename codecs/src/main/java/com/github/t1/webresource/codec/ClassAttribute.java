package com.github.t1.webresource.codec;

import com.github.t1.webresource.codec.AbstractHtmlWriter.Attribute;
import com.github.t1.webresource.meta.*;

public class ClassAttribute extends Attribute {
    public ClassAttribute(Trait trait) {
        this(trait, null);
    }

    public ClassAttribute(Trait trait, String suffix) {
        this(new FieldName(trait), suffix);
    }

    public ClassAttribute(Item item) {
        this(item.type(), null);
    }

    private ClassAttribute(CharSequence prefix, String suffix) {
        super("class", prefix + ((suffix == null) ? "" : ("-" + suffix)));
    }
}
