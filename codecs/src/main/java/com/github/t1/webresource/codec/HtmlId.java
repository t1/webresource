package com.github.t1.webresource.codec;

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
        return item.get(trait).toString();
    }

    public Trait trait() {
        Trait webResourceKeyTrait = getWebResourceKey();
        if (webResourceKeyTrait != null)
            return webResourceKeyTrait;

        List<Trait> traits = item.trait(Id.class);
        if (traits.isEmpty())
            return null;
        if (traits.size() > 1)
            throw new RuntimeException("found more than one id traits: " + traits);
        return traits.get(0);
    }

    public Trait getWebResourceKey() {
        List<Trait> traits = item.trait(WebResourceKey.class);
        if (traits.isEmpty())
            return null;
        if (traits.size() > 1)
            throw new RuntimeException("found more than one WebResourceKey traits: " + traits);
        log.debug("found webresource key trait {}", traits);
        return traits.get(0);
    }
}
