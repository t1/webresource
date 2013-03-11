package com.github.t1.webresource;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InputStream;

import javax.lang.model.element.*;

import lombok.Data;
import lombok.Delegate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class WebResourceWriterTest {
    @Data
    private static class TestName implements Name {
        @Delegate
        final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    @Mock
    TypeElement type;
    @Mock
    PackageElement pkg;

    @Test
    public void shouldGenerateTheSame() throws Exception {
        when(type.getEnclosingElement()).thenReturn(pkg);
        when(type.getSimpleName()).thenReturn(new TestName("TestEntity"));

        when(pkg.getKind()).thenReturn(ElementKind.PACKAGE);
        when(pkg.getQualifiedName()).thenReturn(new TestName("com.github.t1.webresource"));

        String oldGenerator = readReference();
        String newGenerator = new WebResourceWriter(type).run();

        assertEquals(oldGenerator, newGenerator);
    }

    private String readReference() throws IOException {
        InputStream inputStream = WebResourceWriterTest.class.getResourceAsStream("TestEntityWebResource.txt");
        StringBuilder result = new StringBuilder();
        while (true) {
            int c = inputStream.read();
            if (c < 0)
                break;
            result.appendCodePoint(c);
        }
        return result.toString();
    }
}