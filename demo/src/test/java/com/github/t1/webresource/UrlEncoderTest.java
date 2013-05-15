package com.github.t1.webresource;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import lombok.*;

import org.junit.Test;

import com.google.common.collect.Maps;

public class UrlEncoderTest {

    private final Writer out = new StringWriter();
    private final UrlEncoder writer = new UrlEncoder(out);

    @Test
    public void shouldNotWriteNull() throws Exception {
        writer.write(null);

        assertEquals("", out.toString());
    }

    @Test
    public void shouldWriteString() throws Exception {
        writer.write("string");

        assertEquals("string", out.toString());
    }

    @Test
    public void shouldEscapeSpace() throws Exception {
        writer.write("string with-blank");

        assertEquals("string%20with-blank", out.toString());
    }

    @Test
    public void shouldEscapeAmpersand() throws Exception {
        writer.write("string&with-ampersand");

        assertEquals("string%26with-ampersand", out.toString());
    }

    @Test
    public void shouldEscapeEquals() throws Exception {
        writer.write("string=with-equals");

        assertEquals("string%3dwith-equals", out.toString());
    }

    @Test
    public void shouldEscapePercent() throws Exception {
        writer.write("string%with-percent");

        assertEquals("string%25with-percent", out.toString());
    }

    @Test
    public void shouldWriteInteger() throws Exception {
        writer.write(123);

        assertEquals("123", out.toString());
    }

    @Test
    public void shouldWriteBoolean() throws Exception {
        writer.write(true);

        assertEquals("true", out.toString());
    }

    @Test
    public void shouldWriteEmptyList() throws Exception {

        writer.write(Collections.emptyList());

        assertEquals("", out.toString());
    }

    @Test
    public void shouldWriteOneElementList() throws Exception {
        writer.write(Arrays.asList("one"));

        assertEquals("one", out.toString());
    }

    @Test
    public void shouldWriteTwoElementList() throws Exception {
        writer.write(Arrays.asList("one", "two"));

        assertEquals("one&two", out.toString());
    }

    @Test
    public void shouldWriteThreeElementList() throws Exception {
        writer.write(Arrays.asList("one", "two", "three"));

        assertEquals("one&two&three", out.toString());
    }

    @Test
    public void shouldWriteEmptyMap() throws Exception {
        Map<Integer, Double> map = Maps.newHashMap();

        writer.write(map);

        assertEquals("", out.toString());
    }

    @Test
    public void shouldWriteOneElementMap() throws Exception {
        Map<Integer, Double> map = Maps.newHashMap();
        map.put(123, 4.5);

        writer.write(map);

        assertEquals("123=4.5", out.toString());
    }

    @Test
    public void shouldWriteTwoElementMap() throws Exception {
        Map<Integer, Double> map = Maps.newLinkedHashMap(); // stable ordering
        map.put(123, 4.5);
        map.put(67, 8.9);

        writer.write(map);

        assertEquals("123=4.5&67=8.9", out.toString());
    }

    @Test
    public void shouldWriteThreeElementMap() throws Exception {
        Map<Integer, Double> map = Maps.newLinkedHashMap(); // stable ordering
        map.put(1, 2.3);
        map.put(4, 5.6);
        map.put(7, 8.9);

        writer.write(map);

        assertEquals("1=2.3&4=5.6&7=8.9", out.toString());
    }

    @Data
    @AllArgsConstructor
    private static class OneFieldPojo {
        private String string;
    }

    @Test
    public void shouldWriteOneFieldPojoWithoutKey() throws Exception {
        OneFieldPojo pojo = new OneFieldPojo("str");

        writer.write(pojo);

        assertEquals("str", out.toString());
    }

    @Test
    public void shouldWriteOneFieldPojoListWithoutKeys() throws Exception {
        OneFieldPojo pojo1 = new OneFieldPojo("one");
        OneFieldPojo pojo2 = new OneFieldPojo("two");
        List<OneFieldPojo> list = Arrays.asList(pojo1, pojo2);

        writer.write(list);

        assertEquals("one&two", out.toString());
    }

    @Data
    @AllArgsConstructor
    private static class TwoFieldPojo {
        private String string;
        private Integer integer;
    }

    @Test
    public void shouldWriteTwoFieldPojoLikeMap() throws Exception {
        TwoFieldPojo pojo = new TwoFieldPojo("str", 123);

        writer.write(pojo);

        assertEquals("string=str&integer=123", out.toString());
    }

    @Test
    public void shouldWriteTwoFieldPojoListLikeMap() throws Exception {
        TwoFieldPojo pojo1 = new TwoFieldPojo("one", 111);
        TwoFieldPojo pojo2 = new TwoFieldPojo("two", 222);
        List<TwoFieldPojo> list = Arrays.asList(pojo1, pojo2);

        writer.write(list);

        assertEquals("string%3done%26integer%3d111&string%3dtwo%26integer%3d222", out.toString());
    }

    @Data
    @AllArgsConstructor
    private static class ThreeFieldPojo {
        private String string;
        private Integer integer;
        private Boolean bool;
    }

    @Test
    public void shouldWriteThreeFieldPojoAsMap() throws Exception {
        ThreeFieldPojo pojo = new ThreeFieldPojo("str", 123, true);

        writer.write(pojo);

        assertEquals("string=str&integer=123&bool=true", out.toString());
    }

    @Test
    public void shouldWriteThreeFieldPojoListEscaped() throws Exception {
        ThreeFieldPojo pojo1 = new ThreeFieldPojo("one", 111, true);
        ThreeFieldPojo pojo2 = new ThreeFieldPojo("two", 222, false);
        List<ThreeFieldPojo> list = Arrays.asList(pojo1, pojo2);

        writer.write(list);

        assertEquals("string%3done%26integer%3d111%26bool%3dtrue&string%3dtwo%26integer%3d222%26bool%3dfalse",
                out.toString());
    }

    @Data
    @AllArgsConstructor
    private static class ThreeFieldPojoWithStatic implements Serializable {
        private static final long serialVersionUID = 1L;
        private String string;
        private Integer integer;
        private Boolean bool;
    }

    @Test
    public void shouldWriteThreeFieldPojoWithStatic() throws Exception {
        ThreeFieldPojoWithStatic pojo = new ThreeFieldPojoWithStatic("str", 123, true);

        writer.write(pojo);

        assertEquals("string=str&integer=123&bool=true", out.toString());
    }

    @Data
    @AllArgsConstructor
    private static class ThreeFieldPojoWithTransient {
        private transient long id;
        private String string;
        private Integer integer;
        private Boolean bool;
    }

    @Test
    public void shouldWriteThreeFieldPojoWithTransient() throws Exception {
        ThreeFieldPojoWithTransient pojo = new ThreeFieldPojoWithTransient(5, "str", 123, true);

        writer.write(pojo);

        assertEquals("string=str&integer=123&bool=true", out.toString());
    }

    // TODO shouldWriteNestedPojoList

    // TODO shouldWritePojoMapKey
    // TODO shouldWritePojoMapValue

    // TODO shouldWriteInheritedPojoFields
}
