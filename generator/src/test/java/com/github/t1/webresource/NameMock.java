package com.github.t1.webresource;

import javax.lang.model.element.Name;

import lombok.Data;

@Data
class NameMock implements Name {
    private final String name;

    public NameMock(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public char charAt(int index) {
        return name.charAt(index);
    }

    @Override
    public int length() {
        return name.length();
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return name.subSequence(start, end);
    }

    @Override
    public boolean contentEquals(CharSequence cs) {
        return name.contentEquals(cs);
    }
}