package com.github.t1.webresource.codec;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.*;

import lombok.*;

import org.junit.*;

public class HtmlHeadWriterTest extends AbstractHtmlWriterTest {
    private void write(Object t) {
        write(HtmlHeadWriter.class, t);
    }

    @AllArgsConstructor
    private static class PojoWithOneHtmlHead {
        @HtmlHead
        public String str;
        public Integer i;
    }

    @Test
    public void shouldWritePojoWithOneHtmlHead() throws Exception {
        PojoWithOneHtmlHead pojo = new PojoWithOneHtmlHead("dummy", 123);
        assertEquals(123, (int) pojo.i); // cover

        write(pojo);

        assertThat(result(), containsString("<title>dummy</title>"));
    }

    @AllArgsConstructor
    private static class PojoWithTwoHtmlHeads {
        @HtmlHead
        public String str0;
        @HtmlHead
        public String str1;
    }

    @Test
    public void shouldWritePojoWithTwoHtmlHead() throws Exception {
        PojoWithTwoHtmlHeads pojo = new PojoWithTwoHtmlHeads("dummy0", "dummy1");

        write(pojo);

        assertThat(result(), containsString("<title>dummy0 - dummy1</title>"));
    }

    @Data
    @AllArgsConstructor
    @HtmlStyleSheet("/root-path")
    private static class PojoWithRootPathCss {
        private String str;
    }

    @Test
    public void shouldLinkRootPathCssStyleSheet() throws Exception {
        PojoWithRootPathCss pojo = new PojoWithRootPathCss("dummy");

        write(pojo);

        assertThat(result(), containsString(stylesheet("/root-path")));
    }

    private String stylesheet(String href) {
        return "<link rel='stylesheet' href='" + href + "' type='text/css'/>";
    }

    @Test
    public void shouldWritePojoListWithRootPathCssStyleSheet() throws Exception {
        PojoWithRootPathCss pojo1 = new PojoWithRootPathCss("a");
        PojoWithRootPathCss pojo2 = new PojoWithRootPathCss("b");
        List<PojoWithRootPathCss> list = Arrays.asList(pojo1, pojo2);

        write(list);

        assertThat(result(), containsString(stylesheet("/root-path")));
    }

    @Data
    @AllArgsConstructor
    @HtmlStyleSheet(value = "file:src/test/resources/testfile.txt", inline = true)
    private static class PojoWithInlineFileCss {
        private String str;
    }

    @Test
    public void shouldInlineFileCssStyleSheet() throws Exception {
        PojoWithInlineFileCss pojo = new PojoWithInlineFileCss("dummy");

        write(pojo);

        assertThat(result(), containsString("<style>test-file-contents</style>"));
    }

    @Data
    @AllArgsConstructor
    @HtmlStyleSheet(value = "/stylesheets/main.css", inline = true)
    private static class PojoWithInlineRootResourceCss {
        private String str;
    }

    @Test
    @Ignore("needs a running service and I have no idea how to map that to a file-url")
    public void shouldInlineRootResourceCssStyleSheet() throws Exception {
        PojoWithInlineRootResourceCss pojo = new PojoWithInlineRootResourceCss("dummy");

        write(pojo);

        assertThat(result(), containsString("<head><style>test-file-contents</style></head>"));
    }

    @Data
    @AllArgsConstructor
    @HtmlStyleSheet(value = "stylesheets/main.css", inline = true)
    private static class PojoWithInlineLocalResourceCss {
        private String str;
    }

    @Test
    @Ignore("needs a running service and I have no idea how to map that to a file-url")
    public void shouldInlineLocalResourceCssStyleSheet() throws Exception {
        PojoWithInlineLocalResourceCss pojo = new PojoWithInlineLocalResourceCss("dummy");

        write(pojo);

        assertThat(result(), containsString("<style>test-file-contents</style>"));
    }

    @Data
    @AllArgsConstructor
    @HtmlStyleSheet("local-path")
    private static class PojoWithLocalCss {
        private String str;
    }

    @Test
    public void shouldLinkLocalCssStyleSheet() throws Exception {
        PojoWithLocalCss pojo = new PojoWithLocalCss("dummy");

        write(pojo);

        assertThat(result(), containsString(stylesheet("/demo/local-path")));
    }

    @Data
    @AllArgsConstructor
    @HtmlStyleSheets({ @HtmlStyleSheet("/root-path"), @HtmlStyleSheet("local-path") })
    private static class PojoWithTwoCss {
        private String str;
    }

    @Test
    public void shouldLinkTwoCssStyleSheets() throws Exception {
        PojoWithTwoCss pojo = new PojoWithTwoCss("dummy");

        write(pojo);

        assertThat(result(), containsString(stylesheet("/root-path")));
        assertThat(result(), containsString(stylesheet("/demo/local-path")));
    }
}
