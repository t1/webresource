package com.github.t1.webresource.typewriter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Encapsulates details about types passed in as a String or Class.
 * <p/>
 * Not smart enough for doubly nested type variables
 */
public class TypeString {
    private static final String ID = "\\p{Alpha}\\p{Alnum}*";
    private static final String TYPE = ID + "(\\." + ID + ")*";
    private static final Pattern TYPE_PATTERN = Pattern.compile("(" + TYPE + ")(<(" + TYPE + "(, " + TYPE + ")*)>)?");
    private static final List<String> COLLECTION_TYPES = Arrays.asList("Set", "List", "Collection");

    private final Matcher matcher;

    /** Is this a non-primitive type, i.e. can it be null */
    public final boolean nullable;
    /** The unqualified type name */
    public final String simple;
    /** The full type name, without generics */
    public final String raw;
    public final String generic;
    /** Is this a collection type, i.e. List, Set, etc. */
    public final boolean isCollection;
    /** The type of the contents of a collection type, or this, if not. */
    public final TypeString uncollected;
    public final List<String> imports = new ArrayList<>();

    public TypeString(Class<?> type) {
        this(type.getName());
    }

    public TypeString(String string) {
        this.matcher = TYPE_PATTERN.matcher(string);
        if (!matcher.matches())
            throw new IllegalArgumentException("invalid type string: [" + string + "]");
        this.nullable = rawType().contains(".");
        this.simple = simpleType(rawType());
        this.generic = genericType();
        this.raw = rawType();
        this.isCollection = isCollection();
        this.uncollected = (isCollection) ? new TypeString(typeArguments()) : this;

        addImports();
    }

    private String rawType() {
        return matcher.group(1);
    }

    private String typeArguments() {
        return matcher.group(4);
    }

    private String genericType() {
        StringBuilder out = new StringBuilder();
        out.append(simpleType(rawType()));

        if (typeArguments() != null) {
            boolean first = true;
            for (String typeArgument : typeArguments().split(", ?")) {
                if (first) {
                    first = false;
                    out.append("<");
                } else {
                    out.append(", ");
                }
                out.append(simpleType(typeArgument));
            }
            out.append(">");
        }
        return out.toString();
    }

    private String simpleType(String type) {
        int index = type.lastIndexOf('.');
        return (index < 0) ? type : type.substring(index + 1);
    }

    private boolean isCollection() {
        return COLLECTION_TYPES.contains(simple);
    }

    private void addImports() {
        addImport(rawType());

        if (typeArguments() != null) {
            for (String typeArgument : typeArguments().split(", ?")) {
                addImport(typeArgument);
            }
        }
    }

    private void addImport(String type) {
        if (nullable && type != null && !type.startsWith("java.lang.")) {
            this.imports.add(type);
        }
    }
}
