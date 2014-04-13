package com.github.t1.webresource.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.*;

/**
 * POJO meta object that provides data as {@link Trait}s and meta data as {@link Annotation}s, i.e. you don't have to
 * work with fields, getters, setters, etc. but work on a meta level. {@link #isSimple() Simple types} (incl.
 * <code>null</code>) are represented as items with one trait. Maps are represented just like objects (note that for
 * pojos and maps, the order of the traits is generally <i>not</i> guaranteed). {@link #isList() List} elements can be
 * accessed with {@link #list()} that returns a list of Items.
 * <p/>
 * Design Decision: The implementation of this interface is currently based on reflection on POJOs, but the API is quite
 * generic, so it should be easy to extract an interface and write implementations that are based on e.g., xml, json,
 * csv, maps, or any other data structure with some sort of meta data facility, internal or external to the data itself.
 * It would be consequential to use some abstraction for meta data instead of annotations, but that would add complexity
 * without adding a lot of utility: Annotations are convenient to represent other meta data as well in a typesafe way,
 * e.g. by using <code>javax.enterprise.util.AnnotationLiteral</code>.
 */
public interface Item {

    /** Is this item <code>null</code>? */
    public boolean isNull();

    /** Is this item "simple", i.e. a string, boolean or a number? */
    public boolean isSimple();

    /** Is this item a list of items? */
    public boolean isList();

    /** Is this item an item type (a.k.a. class)? */
    public boolean isType();

    /** The list of elements in this {@link #isList() list-trait} */
    public List<Item> list();

    /** The publicly visible traits */
    public Collection<Trait> traits();

    /** The value of that trait */
    public Item read(Trait trait);

    /** Sets the value of that trait */
    public void write(Trait trait, Item value);

    /** The trait with that name... visible or not, transient or not */
    public Trait trait(String traitName);

    /** The type name of this item */
    public String type();

    /**
     * The list of traits with this annotation. Note that this may also return traits that are <i>not</i> in
     * {@link #traits()}, e.g. an invisible or transient id of an entity.
     */
    public <A extends Annotation> List<Trait> trait(Class<A> type);

    /** Is this item annotated as <code>type</code>? */
    public <A extends Annotation> boolean is(Class<A> type);

    /** The item annotation of that <code>type</code> or <code>null</code> if it's not annotated so. */
    public <A extends Annotation> A get(Class<A> type);

    /** The list of all annotations on the item */
    public AnnotatedElement annotations();
}
