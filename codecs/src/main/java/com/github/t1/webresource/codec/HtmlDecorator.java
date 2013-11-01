package com.github.t1.webresource.codec;

import com.github.t1.webresource.meta.Item;

/**
 * Decorate the main item, i.e. write menus, navigation, footers, etc. Generally, decorators will
 * {@link java.lang.Inject inject} the {@link HtmlOut} to write to.<br>
 * Decoration is written <i>after</i> the main item is, so you'll have to locate it on screen using CSS.
 */
public interface HtmlDecorator {
    public void decorate(Item item);
}
