package com.github.t1.webresource;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.*;

import javax.lang.model.element.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class WebResourceWriterTest {
    @Mock
    TypeElement type;
    @Mock
    PackageElement pkg;

    @Test
    public void shouldGenerateTheSame() throws Exception {
        when(type.getEnclosingElement()).thenReturn(pkg);
        when(type.getSimpleName()).thenReturn(new NameMock("TestEntity"));

        when(pkg.getKind()).thenReturn(ElementKind.PACKAGE);
        when(pkg.getQualifiedName()).thenReturn(new NameMock("com.github.t1.webresource"));

        String oldGenerator = readReference();
        String newGenerator = new WebResourceWriter(type).run();

        assertEquals(oldGenerator, newGenerator);
    }

    private String readReference() throws IOException {
        String fileName = "TestEntityWebResource.txt";
        InputStream inputStream = WebResourceWriterTest.class.getResourceAsStream(fileName);
        if (inputStream == null)
            throw new FileNotFoundException(fileName);
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