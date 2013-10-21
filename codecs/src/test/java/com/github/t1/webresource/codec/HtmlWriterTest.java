package com.github.t1.webresource.codec;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.github.t1.webresource.meta.Item;

public class HtmlWriterTest extends AbstractHtmlWriterTest {
    @Test
    public void shouldWriteBody() throws Exception {
        write(new HtmlWriter() {
            @Override
            public void writeBody(Item item) {
                write("{body:" + item + "}");
            }
        }, "dummy");

        assertEquals("<html><head></head><body>{body:dummy}</body></html>", result());
    }

    @Test
    public void shouldWriteHead() throws Exception {
        write(new HtmlWriter() {
            @Override
            public void writeHead(Item item) {
                write("{head:" + item + "}");
            }

            @Override
            public void writeBody(Item item) {}
        }, "dummy");

        assertEquals("<html><head>{head:dummy}</head><body></body></html>", result());
    }

    @Test
    public void shouldWriteExceptionInBody() throws Exception {
        HtmlWriter writer = new HtmlWriter() {
            @Override
            public void writeBody(Item item) {
                throw new RuntimeException("test-exception");
            }
        };
        try {
            write(writer, "dummy");
            fail("expected RuntimeException");
        } catch (RuntimeException e) {
            // we don't check the exception but the html output
        }

        // TODO the error text should be IN the body
        assertThat(result(), startsWith("<html><head></head><body></body>error writing body</html>"));
    }

    @Test
    public void shouldWriteExceptionInHead() throws Exception {
        HtmlWriter writer = new HtmlWriter() {
            @Override
            public void writeHead(Item item) {
                throw new RuntimeException("test-exception");
            }
        };
        try {
            write(writer, "dummy");
            fail("expected RuntimeException");
        } catch (RuntimeException e) {
            // we don't check the exception but the html output
        }

        // TODO the error text should be IN the head
        assertThat(result(), startsWith("<html><head></head>error writing head</html>"));
    }
}
