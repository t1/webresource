package com.github.t1.webresource;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A helper for {@link WebResourceField}s, encapsulating everything about types.
 * <p/>
 * Not smart enough for doubly nested type variables
 */
class TypeString {
    public static Class<?> type(String typeName) {
        switch (typeName) {
        case "long":
            return Long.TYPE;
        case "int":
            return Integer.TYPE;
        case "short":
            return Short.TYPE;
        case "byte":
            return Byte.TYPE;
        case "char":
            return Character.TYPE;
        case "double":
            return Double.TYPE;
        case "float":
            return Float.TYPE;
        case "boolean":
            return Boolean.TYPE;
        case "void":
            return Void.TYPE;
        default:
            return forName(typeName);
        }
    }

    private static Class<?> forName(String typeName) {
        try {
            return Class.forName(typeName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String ID = "\\p{Alpha}\\p{Alnum}*";
    private static final String TYPE = ID + "(\\." + ID + ")*";
    private static final Pattern TYPE_PATTERN = Pattern.compile("(" + TYPE + ")(<(" + TYPE + "(, " + TYPE + ")*)>)?");

    private final Matcher matcher;

    final boolean nullable;
    final String simpleType;
    final boolean isCollection;
    final Class<?> rawType;
    final Class<?> uncollectedType;
    final List<String> imports = new ArrayList<String>();

    public TypeString(String string) {
        this.matcher = TYPE_PATTERN.matcher(string);
        if (!matcher.matches())
            throw new IllegalArgumentException("invalid type string: [" + string + "]");
        this.nullable = rawType().contains(".");
        this.simpleType = simpleType();
        this.rawType = type(rawType());
        this.isCollection = isCollection();
        this.uncollectedType = type((isCollection) ? typeArguments() : rawType());

        addImports();
    }

    private String rawType() {
        return matcher.group(1);
    }

    private String typeArguments() {
        return matcher.group(4);
    }

    private String simpleType() {
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
        return Collection.class.isAssignableFrom(rawType);
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
