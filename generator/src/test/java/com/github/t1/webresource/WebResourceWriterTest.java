package com.github.t1.webresource;

import static com.github.t1.webresource.IdTypeTest.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.util.Arrays;

import javax.annotation.processing.Messager;
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
    @Mock
    Messager messager;

    @Test
    public void shouldGenerateTheSame() throws Exception {
        String packageName = "com.github.t1.webresource";
        String typeName = "TestEntity";

        when(type.getQualifiedName()).thenReturn(new NameMock(packageName + "." + typeName));
        when(type.getEnclosingElement()).thenReturn(pkg);
        when(type.getSimpleName()).thenReturn(new NameMock(typeName));

        Element field = mockField();
        mockFieldType(field, "long");
        doReturn(Arrays.asList(field)).when(type).getEnclosedElements();

        when(pkg.getKind()).thenReturn(ElementKind.PACKAGE);
        when(pkg.getQualifiedName()).thenReturn(new NameMock(packageName));

        String expected = readReference();
        String generated = new WebResourceWriter(messager, type).run();

        assertEquals(expected, generated);
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