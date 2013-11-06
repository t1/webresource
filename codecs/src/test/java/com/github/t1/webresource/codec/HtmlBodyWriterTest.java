package com.github.t1.webresource.codec;

import static java.util.Arrays.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.t1.webresource.meta.*;

@RunWith(MockitoJUnitRunner.class)
public class HtmlBodyWriterTest extends AbstractHtmlWriterTest {

    @Mock
    HtmlFormWriter formWriter;
    @InjectMocks
    HtmlBodyWriter writer;

    private void write(Object t) {
        writer.out = out;
        writer.write(Items.newItem(t));
    }

    @Test
    public void shouldEncodeNullObject() throws Exception {
        write(null);

        assertEquals("", result());
    }

    @Test
    public void shouldEncodePrimitiveString() throws Exception {
        write("dummy");

        assertEquals("dummy", result());
    }

    @Test
    public void shouldEscapeString() throws Exception {
        write("string & ampersand");

        assertEquals("string &amp; ampersand", result());
    }

    @Test
    public void shouldEncodePrimitiveInteger() throws Exception {
        write(1234);

        assertEquals("1234", result());
    }

    @Test
    public void shouldEncodeEmptyList() throws Exception {
        List<String> list = Collections.emptyList();

        write(list);

        assertEquals("", result());
    }

    @Test
    public void shouldEncodeNoTraitItem() throws Exception {
        List<NoTraitPojo> list = asList(new NoTraitPojo(), new NoTraitPojo());

        write(list);

        assertEquals("", result());
    }

    @Test
    public void shouldEncodeStringList() throws Exception {
        List<String> list = asList("one", "two", "three");

        write(list);

        assertEquals(ul("strings", "one", "two", "three"), result());
    }

    @Test
    public void shouldEncodeTable() throws Exception {
        List<OneStringPojo> list =
                asList(new OneStringPojo("one"), new OneStringPojo("two"), new OneStringPojo("three"));

        write(list);

        assertEquals("<table><thead><tr><th>string</th></tr></thead><tbody>"
                + "<tr><td>one</td></tr><tr><td>two</td></tr><tr><td>three</td></tr></tbody></table>", result());
    }

    @Test
    public void shouldEncodeForm() throws Exception {
        doAnswer(writeAnswer("body")).when(formWriter).write(any(Item.class));
        NoTraitPojo pojo = new NoTraitPojo();

        write(pojo);

        assertEquals("{body:the-pojo}", result());
    }

    @Test
    public void shouldLinkToType() throws Exception {
        write(NoTraitPojo.class);

        assertEquals("{link:notraitpojos}", result());
    }
}
