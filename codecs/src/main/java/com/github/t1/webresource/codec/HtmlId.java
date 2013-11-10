package com.github.t1.webresource.codec;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.persistence.Id;

import lombok.extern.slf4j.Slf4j;

import com.github.t1.webresource.WebResourceKey;
import com.github.t1.webresource.meta.*;

@Slf4j
public class HtmlId {
    public static HtmlId of(Item item) {
        return new HtmlId(item);
    }

    private final Item item;

    public HtmlId(Item item) {
        this.item = item;
    }

    @Override
    public String toString() {
        Trait trait = trait();
        if (trait == null)
            return item.toString();
        return item.read(trait).toString();
    }

    public Trait trait() {
        Trait webResourceKeyTrait = trait(WebResourceKey.class);
        if (webResourceKeyTrait != null)
            return webResourceKeyTrait;

        return trait(Id.class);
    }

    public Trait trait(Class<? extends Annotation> type) {
        List<Trait> traits = item.trait(type);
        if (traits.isEmpty())
            return null;
        if (traits.size() > 1)
            throw new RuntimeException("found more than one " + type.getSimpleName() + " traits: " + traits);
        Trait trait = traits.get(0);
        log.debug("found {} key trait {}", type, trait);
        return trait;
    }
}
