package com.github.t1.webresource;

import static java.util.Arrays.*;
import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import javax.xml.bind.annotation.XmlTransient;

import lombok.*;

import org.junit.Test;

public class HtmlEncoderTest {
    private final Writer out = new StringWriter();
    private final HtmlEncoder writer = new HtmlEncoder(out);

    private static String wrapped(String string) {
        return "<html><head></head><body>" + string + "</body></html>";
    }

    private String result() {
        return out.toString().replaceAll("\n", "");
    }

    @Test
    public void shouldEncodeNullObject() throws Exception {
        writer.write(null);

        assertEquals(wrapped(""), result());
    }

    @Test
    public void shouldEncodePrimitiveString() throws Exception {
        writer.write("dummy");

        assertEquals(wrapped("dummy"), result());
    }

    @Test
    public void shouldEncodePrimitiveInteger() throws Exception {
        writer.write(1234);

        assertEquals(wrapped("1234"), result());
    }

    @Test
    public void shouldEncodeList() throws Exception {
        writer.write(asList("one", "two", "three"));

        assertEquals(wrapped("<ul><li>one</li><li>two</li><li>three</li></ul>"), result());
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

        assertEquals(wrapped("str"), result());
    }

    @Test
    public void shouldWriteOneFieldPojoNullValue() throws Exception {
        OneFieldPojo pojo = new OneFieldPojo(null);

        writer.write(pojo);

        assertEquals(wrapped(""), result());
    }

    @Test
    public void shouldWriteOneFieldPojoListAsUnorderedList() throws Exception {
        OneFieldPojo pojo1 = new OneFieldPojo("one");
        OneFieldPojo pojo2 = new OneFieldPojo("two");
        OneFieldPojo pojo3 = new OneFieldPojo("three");
        List<OneFieldPojo> list = asList(pojo1, pojo2, pojo3);

        writer.write(list);

        assertEquals(wrapped("<ul><li>one</li><li>two</li><li>three</li></ul>"), result());
    }

    @Data
    @AllArgsConstructor
    private static class TwoFieldPojo {
        private String str;
        private Integer i;
    }

    @Test
    public void shouldWriteTwoFieldPojoAsSequenceOfDivsWithLabelsAndReadonlyInputs() throws Exception {
        TwoFieldPojo pojo = new TwoFieldPojo("dummy", 123);

        writer.write(pojo);

        assertEquals(wrapped("" //
                + "<div>" //
                + "<label for='str-0'>str</label>" //
                + "<input id='str-0' type='text' value='dummy' readonly/>" //
                + "</div><div>" //
                + "<label for='i-0'>i</label>" //
                + "<input id='i-0' type='text' value='123' readonly/>" //
                + "</div>"), result());
    }

    @Test
    public void shouldWriteTwoFieldPojoListAsTables() throws Exception {
        TwoFieldPojo pojo1 = new TwoFieldPojo("one", 111);
        TwoFieldPojo pojo2 = new TwoFieldPojo("two", 222);
        List<TwoFieldPojo> list = Arrays.asList(pojo1, pojo2);

        writer.write(list);

        assertEquals(wrapped("<table>" //
                + "<tr><td>str</td><td>i</td></tr>" //
                + "<tr><td>one</td><td>111</td></tr>" //
                + "<tr><td>two</td><td>222</td></tr>" //
                + "</table>"), result());
    }

    @Data
    @AllArgsConstructor
    private static class PojoWithXmlTransient {
        @XmlTransient
        private String id;
        private String str;
        private Integer i;
    }

    @Test
    public void shouldWritePojoWithXmlTransient() throws Exception {
        PojoWithXmlTransient pojo = new PojoWithXmlTransient("id", "dummy", 123);

        writer.write(pojo);

        assertEquals(wrapped("" //
                + "<div>" //
                + "<label for='str-0'>str</label>" //
                + "<input id='str-0' type='text' value='dummy' readonly/>" //
                + "</div><div>" //
                + "<label for='i-0'>i</label>" //
                + "<input id='i-0' type='text' value='123' readonly/>" //
                + "</div>"), result());
    }

    @Test
    public void shouldWritePojoWithTransientList() throws Exception {
        PojoWithXmlTransient pojo1 = new PojoWithXmlTransient("a", "one", 111);
        PojoWithXmlTransient pojo2 = new PojoWithXmlTransient("b", "two", 222);
        List<PojoWithXmlTransient> list = Arrays.asList(pojo1, pojo2);

        writer.write(list);

        assertEquals(wrapped("<table>" //
                + "<tr><td>str</td><td>i</td></tr>" //
                + "<tr><td>one</td><td>111</td></tr>" //
                + "<tr><td>two</td><td>222</td></tr>" //
                + "</table>"), result());
    }
}
