package com.github.t1.webresource;

import static com.github.t1.webresource.IdTypeTest.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.util.Arrays;

import javax.annotation.processing.Messager;
import javax.enterprise.util.AnnotationLiteral;
import javax.lang.model.element.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class WebResourceWriterTest {
    @SuppressWarnings("all")
    private static class WebResourceLiteral extends AnnotationLiteral<WebResource> implements WebResource {
        private static final long serialVersionUID = 1L;
        private final boolean extended;

        public WebResourceLiteral(boolean extended) {
            this.extended = extended;
        }

        @Override
        public boolean extended() {
            return extended;
        }
    }

    @Mock
    TypeElement type;
    @Mock
    PackageElement pkg;
    @Mock
    Messager messager;

    @Test
    public void shouldGenerateExtended() throws Exception {
        shouldGenerate(true);
    }

    @Test
    public void shouldGenerateNonExtended() throws Exception {
        shouldGenerate(false);
    }

    private void shouldGenerate(boolean extended) throws Exception {
        String packageName = "com.github.t1.webresource";
        String typeName = "TestEntity";

        when(type.getQualifiedName()).thenReturn(new NameMock(packageName + "." + typeName));
        when(type.getEnclosingElement()).thenReturn(pkg);
        when(type.getSimpleName()).thenReturn(new NameMock(typeName));
        when(type.getAnnotation(WebResource.class)).thenReturn(new WebResourceLiteral(extended));

        Element field = mockField();
        mockFieldType(field, "long", "id");
        doReturn(Arrays.asList(field)).when(type).getEnclosedElements();

        when(pkg.getKind()).thenReturn(ElementKind.PACKAGE);
        when(pkg.getQualifiedName()).thenReturn(new NameMock(packageName));

        String expected = readReference().replace("${extended}",
                extended ? "(type = PersistenceContextType.EXTENDED)" : "");
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
