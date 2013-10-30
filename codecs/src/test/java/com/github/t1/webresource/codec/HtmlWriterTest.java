package com.github.t1.webresource.codec;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.github.t1.webresource.meta.Item;

@RunWith(MockitoJUnitRunner.class)
public class HtmlWriterTest extends AbstractHtmlWriterTest {
    private static final String EXCEPTION_COMMENT = //
            "<!-- ............................................................" //
                    + "java.lang.RuntimeException" //
                    + "............................................................ -->";

    @Mock
    HtmlBodyWriter bodyWriter;
    @Mock
    HtmlHeadWriter headWriter;
    @InjectMocks
    HtmlWriter writer;

    private Answer<Void> write(final String prefix) {
        return new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                out.write("{" + prefix + ":" + invocation.getArguments()[0] + "}");
                return null;
            }
        };
    }

    @Test
    public void shouldWriteBody() throws Exception {
        doAnswer(write("body")).when(bodyWriter).write(any(Item.class));

        write(writer, "dummy");

        assertEquals("<html><head></head><body>{body:dummy}</body></html>", result());
    }

    @Test
    public void shouldWriteHead() throws Exception {
        doAnswer(write("head")).when(headWriter).write(any(Item.class));

        write(writer, "dummy");

        assertEquals("<html><head>{head:dummy}</head><body></body></html>", result());
    }

    @Test
    public void shouldWriteExceptionInBody() throws Exception {
        doThrow(RuntimeException.class).when(bodyWriter).write(any(Item.class));

        try {
            write(writer, "dummy");
            fail("expected RuntimeException");
        } catch (RuntimeException e) {
            // we don't check the exception but the html output
        }

        // TODO the error text should be IN the body
        assertEquals("<html><head></head><body></body>error writing body</html>" + EXCEPTION_COMMENT, result());
    }

    @Test
    public void shouldWriteExceptionInHead() throws Exception {
        doThrow(RuntimeException.class).when(headWriter).write(any(Item.class));

        try {
            write(writer, "dummy");
            fail("expected RuntimeException");
        } catch (RuntimeException e) {
            // we don't check the exception but the html output
        }

        // TODO the error text should be IN the head
        assertEquals("<html><head></head>error writing head</html>" + EXCEPTION_COMMENT, result());
    }
}
