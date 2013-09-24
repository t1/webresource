package com.github.t1.webresource.meta;


public abstract class ObjectTrait extends AbstractTrait {
    @Override
    public String type() {
        Class<?> typeClass = typeClass();
        if (typeClass == null)
            return "null";
        String type = typeClass.getSimpleName().toLowerCase();
        switch (type) {
            case "byte":
            case "short":
            case "integer":
            case "long":
            case "float":
            case "double":
                return "number";
            case "calendar":
            case "date":
                return "date";
            default:
                return type;
        }
    }

    protected abstract Class<?> typeClass();
}
