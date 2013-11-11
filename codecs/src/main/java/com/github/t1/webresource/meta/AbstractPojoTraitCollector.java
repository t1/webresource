package com.github.t1.webresource.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

import com.github.t1.stereotypes.Annotations;
import com.google.common.base.Predicate;

/**
 * TODO traits of super classes
 */
public abstract class AbstractPojoTraitCollector {
    protected final Class<?> type;
    private final AnnotatedElement annotations;
    private final List<Trait> traits = new ArrayList<>();

    public AbstractPojoTraitCollector(Class<?> type) {
        this.type = type;
        this.annotations = Annotations.on(type);
    }

    protected boolean typeIs(Class<? extends Annotation> annotationType) {
        return annotations.isAnnotationPresent(annotationType);
    }

    protected <T extends Annotation> T typeAnnotation(Class<T> annotationType) {
        return annotations.getAnnotation(annotationType);
    }

    public List<Trait> run() {
        addFields();
        addAccessors();
        sort(traits);
        return traits;
    }

    private void addFields() {
        for (Field field : type.getDeclaredFields()) {
            if (isStatic(field))
                continue;
            add(new PojoFieldTrait(field, traitName(field), fieldVisiblePredicate()));
        }
    }

    private boolean isStatic(Member member) {
        return Modifier.isStatic(member.getModifiers());
    }

    protected String traitName(Field field) {
        return field.getName();
    }

    protected abstract Predicate<PojoTrait> fieldVisiblePredicate();

    protected boolean isPublic(Member member) {
        int modifiers = member.getModifiers();
        return Modifier.isPublic(modifiers);
    }

    private void addAccessors() {
        for (Method method : type.getDeclaredMethods()) {
            if (isStatic(method) || !isGetter(method))
                continue;
            String name = traitName(method);
            AnnotatedElement annotations = Annotations.on(method);
            add(new PojoAccessorTrait(method, annotations, name, accessorVisiblePredicate()));
        }
    }

    /**
     * Is this method a getter? I.e. the name starts with <code>get</code> (or <code>is</code> for booleans), returns a
     * type, and takes no arguments.
     */
    private boolean isGetter(Method method) {
        return method.getParameterTypes().length == 0 && method.getReturnType() != void.class
                && isGetterMethodName(method.getName());
    }

    private boolean isGetterMethodName(String name) {
        return name.startsWith("get") || name.startsWith("is");
    }

    protected String traitName(Method method) {
        String name = method.getName();
        if (name.startsWith("get"))
            name = uncapitalize(name.substring(3));
        else if (name.startsWith("is"))
            name = uncapitalize(name.substring(2));
        return name;
    }

    private static String uncapitalize(String name) {
        if (name.length() < 1)
            return name;
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    protected abstract Predicate<PojoTrait> accessorVisiblePredicate();

    private void add(PojoTrait newTrait) {
        for (Trait existingTrait : traits) {
            if (existingTrait.name().equals(newTrait.name())) {
                if (!existingTrait.visible() && newTrait.visible()) {
                    merge((PojoTrait) existingTrait, newTrait);
                    traits.remove(existingTrait);
                    break;
                } else {
                    merge(newTrait, (PojoTrait) existingTrait);
                    return;
                }
            }
        }
        traits.add(newTrait);
    }

    /** merge the meta data about the new trait into the exisiting trait, esp. annotations */
    private void merge(PojoTrait newTrait, PojoTrait existingTrait) {
        existingTrait.add(newTrait.annotations);
    }

    protected void sort(List<Trait> traits) {}
}
