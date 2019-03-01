package com.github.t1.webresource;

import com.github.t1.webresource.annotations.WebResource;
import com.github.t1.webresource.annotations.WebResourceKey;
import com.github.t1.webresource.annotations.WebSubResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.annotation.processing.Messager;
import javax.enterprise.util.AnnotationLiteral;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.persistence.Entity;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.github.t1.webresource.WebResourceFieldTest.mockField;
import static com.github.t1.webresource.WebResourceFieldTest.mockFieldType;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("SpellCheckingInspection")
@RunWith(MockitoJUnitRunner.class)
public class WebResourceWriterTest {
    @SuppressWarnings("all")
    private static class WebResourceLiteral extends AnnotationLiteral<WebResource> implements WebResource {
        private static final long serialVersionUID = 1L;
        private final boolean extended;
        private final String path;

        public WebResourceLiteral(boolean extended, String path) {
            this.extended = extended;
            this.path = path;
        }

        @Override
        public boolean extended() {
            return extended;
        }

        @Override public String path() {
            return path;
        }
    }

    @Mock
    TypeElement type;
    @Mock
    PackageElement pkg;
    @Mock
    Messager messager;

    private Element idField;
    private List<Element> fields = new ArrayList<>();

    private void mockAnnotationProcessor(boolean extended, String idType, String path) {
        String packageName = "com.github.t1.webresource";
        String typeName = "TestEntity";

        when(type.getQualifiedName()).thenReturn(new NameMock(packageName + "." + typeName));
        when(type.getEnclosingElement()).thenReturn(pkg);
        when(type.getSimpleName()).thenReturn(new NameMock(typeName));

        idField = mockField();
        fields.add(idField);
        mockFieldType(idField, idType);
        doReturn(fields).when(type).getEnclosedElements();

        when(pkg.getKind()).thenReturn(ElementKind.PACKAGE);
        when(pkg.getQualifiedName()).thenReturn(new NameMock(packageName));

        when(type.getAnnotation(WebResource.class)).thenReturn(new WebResourceLiteral(extended, path));
    }

    @Test
    public void shouldGenerateExtended() throws Exception {
        shouldGenerate(true, "");
    }

    @Test
    public void shouldGenerateNonExtended() throws Exception {
        shouldGenerate(false, "");
    }

    @Test
    public void shouldGenerateWithPath() throws Exception {
        shouldGenerate(false, "/path");
    }

    private void shouldGenerate(boolean extended, String path) throws Exception {
        mockAnnotationProcessor(extended, "long", path);

        String generated = new WebResourceWriter(messager, type).run();

        String expected = readReference("TestEntityWebResource-noversion-nokey.txt")
            .replace("${extended}", extended ? "(type = PersistenceContextType.EXTENDED)" : "")
            .replace("${path}", path.isEmpty() ? "/testentities" : path);
        assertEquals(expected, generated);
    }

    private String readReference(String fileName) throws IOException {
        try (InputStream inputStream = WebResourceWriterTest.class.getResourceAsStream(fileName)) {
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

    @Test
    public void shouldGenerateSecondaryKey() throws Exception {
        mockAnnotationProcessor(false, "long", "");

        mockKeyAndVersion();

        String generated = new WebResourceWriter(messager, type).run();

        String expected = readReference("TestEntityWebResource-version-key.txt");
        assertEquals(expected, generated);
    }

    private void mockKeyAndVersion() {
        Element key = mockField();
        mockFieldType(key, "java.lang.String", "key", WebResourceKey.class);

        Element version = mockField();
        mockFieldType(version, "java.lang.Long", "version", javax.persistence.Version.class);

        doReturn(asList(key, idField, version)).when(type).getEnclosedElements();
    }

    @Test
    public void shouldGenerateBigDecimal() throws Exception {
        mockAnnotationProcessor(false, "java.math.BigDecimal", "");
        AnnotationMirror entity = mock(AnnotationMirror.class);
        doReturn(singletonList(entity)).when(type).getAnnotationMirrors();
        DeclaredType declaredType = mock(DeclaredType.class);
        when(declaredType.toString()).thenReturn(Entity.class.getName());
        when(entity.getAnnotationType()).thenReturn(declaredType);
        Map<ExecutableElement, AnnotationValue> map = new LinkedHashMap<>();
        AnnotationValue nameAnnotationValue = mock(AnnotationValue.class, RETURNS_DEEP_STUBS);
        when(nameAnnotationValue.getValue().toString()).thenReturn("TEST_ENTITY");
        ExecutableElement executableElement = mock(ExecutableElement.class);
        when(executableElement.getSimpleName()).thenReturn(new NameMock("name"));
        map.put(executableElement, nameAnnotationValue);
        doReturn(map).when(entity).getElementValues();

        String generated = new WebResourceWriter(messager, type).run();

        String expected = readReference("TestEntityWebResource-bigdecimal-noversion-nokey.txt");
        assertEquals(expected, generated);
    }

    @Test
    public void shouldGenerateSubResource() throws Exception {
        mockAnnotationProcessor(false, "long", "");

        Element subResourceField = mockField();
        mockFieldType(subResourceField, "java.lang.String", "subresource", WebSubResource.class);
        fields.add(subResourceField);

        String generated = new WebResourceWriter(messager, type).run();

        String expected = readReference("TestEntityWebResource-noversion-nokey-subresource.txt");
        assertEquals(expected, generated);
    }

    @Test
    public void shouldGenerateCollectionSubResource() throws Exception {
        mockAnnotationProcessor(false, "long", "");

        Element subResourceField = mockField();
        mockFieldType(subResourceField, "java.util.List<java.lang.String>", "subresource", WebSubResource.class);
        fields.add(subResourceField);

        String generated = new WebResourceWriter(messager, type).run();

        String expected = readReference("TestEntityWebResource-noversion-nokey-coll-subresource.txt");
        assertEquals(expected, generated);
    }
}
