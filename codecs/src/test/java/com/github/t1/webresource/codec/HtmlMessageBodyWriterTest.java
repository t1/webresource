package com.github.t1.webresource.codec;

import com.github.t1.webresource.annotations.WebResourceKey;
import lombok.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.*;
import javax.ws.rs.core.*;
import javax.xml.bind.annotation.XmlTransient;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.file.*;
import java.util.List;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static javax.ws.rs.core.MediaType.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class HtmlMessageBodyWriterTest {
    @InjectMocks HtmlMessageBodyWriter writer;
    @Mock UriInfo uriInfo;

    @Before
    public void setUp() throws Exception {
        when(writer.uriInfo.getBaseUri()).thenReturn(URI.create("http://example.org/app"));
    }

    @SneakyThrows(IOException.class)
    private String write(Object object) {
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Class<?> type = object.getClass();
        Type genericType = type;
        if (object instanceof GenericEntity) {
            genericType = ((GenericEntity) object).getType();
            object = ((GenericEntity) object).getEntity();
        }
        writer.writeTo(object, type, genericType, new Annotation[0], TEXT_HTML_TYPE, headers, stream);
        return stream.toString();
    }

    private String html(String title, String body) {
        return head(title, "") + body(body);
    }

    private String head(String title, String more) {
        return ""
                + "<!DOCTYPE html>\n"
                + "<html>\n"
                + "  <head>\n"
                + "    <meta charset=\"utf-8\"/>\n"
                + "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"/>\n"
                + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/>\n"
                + "\n"
                + "    <title>" + title + "</title>\n"
                + "\n"
                + "    <link rel=\"stylesheet\" href=\"" + writer.bootstrapCssUri() + "\" "
                + /*   */ "integrity=\"" + writer.bootstrapCssIntegrity() + "\" "
                + /*   */ "crossorigin=\"anonymous\"/>\n"
                + more
                + "  </head>\n";
    }

    private String body(String body) {
        return ""
                + "  <body class=\"container-fluid\" style=\"padding-top: 15px\">\n"
                + body
                + "    <script src=\"" + writer.jqueryUri() + "\" "
                + /*   */ "integrity=\"" + writer.jqueryIntegrity() + "\" "
                + /*   */ "crossorigin=\"anonymous\"></script>\n"
                + "    <script src=\"" + writer.bootstrapJsUri() + "\" "
                + /*   */ "integrity=\"" + writer.bootstrapJsIntegrity() + "\" "
                + /*   */ "crossorigin=\"anonymous\"></script>\n"
                + "  </body>\n"
                + "</html>\n";
    }

    @SneakyThrows(IOException.class)
    private void writeToFile(String html) {
        Files.write(Paths.get("target/test-output.html"), html.getBytes());
    }

    @Test
    public void shouldWriteEmptyPojo() {
        class EmptyPojo {}

        String html = write(new EmptyPojo());

        assertThat(html).isEqualTo(html("Empty Pojo", ""));
    }

    @Test
    public void shouldWriteEmptyPojoWithTitle() {
        @HtmlTitle("Hello, World!")
        class EmptyPojo {}

        String html = write(new EmptyPojo());

        assertThat(html).isEqualTo(html("Hello, World!", ""));
    }

    @Test
    public void shouldWriteSimplePojo() throws IOException {
        @Data
        class SimplePojo {
            String one = "a", two = "b";
            int three = 3;
        }

        String html = write(new SimplePojo());

        assertThat(html).isEqualTo(html("Simple Pojo", ""
                + "    <dl class=\"dl-horizontal simple-pojo\">\n"
                + "      <dt class=\"simple-pojo\" id=\"one\">one</dt>\n"
                + "      <dd class=\"simple-pojo\" id=\"one\">a</dd>\n"
                + "\n"
                + "      <dt class=\"simple-pojo\" id=\"two\">two</dt>\n"
                + "      <dd class=\"simple-pojo\" id=\"two\">b</dd>\n"
                + "\n"
                + "      <dt class=\"simple-pojo\" id=\"three\">three</dt>\n"
                + "      <dd class=\"simple-pojo\" id=\"three\">3</dd>\n"
                + "    </dl>\n"));
    }

    @Test
    public void shouldHtmlEscapeValueString() throws IOException {
        @Data
        class StyledPojo {
            String one = "\"'&<>";
        }

        String html = write(new StyledPojo());

        assertThat(html).isEqualTo(html("Styled Pojo", ""
                + "    <dl class=\"dl-horizontal styled-pojo\">\n"
                + "      <dt class=\"styled-pojo\" id=\"one\">one</dt>\n"
                + "      <dd class=\"styled-pojo\" id=\"one\">&quot;&apos;&amp;&lt;&gt;</dd>\n"
                + "    </dl>\n"));
    }

    @Test
    public void shouldWriteSimplePojoWithStyleSheet() throws IOException {
        @Data
        @HtmlStyleSheet("/foo.css")
        class StyledPojo {
            String one = "a";
        }

        String html = write(new StyledPojo());

        assertThat(html).isEqualTo(head("Styled Pojo", ""
                + "    <link rel=\"stylesheet\" href=\"http://example.org/foo.css\"/>\n")
                + body(""
                + "    <dl class=\"dl-horizontal styled-pojo\">\n"
                + "      <dt class=\"styled-pojo\" id=\"one\">one</dt>\n"
                + "      <dd class=\"styled-pojo\" id=\"one\">a</dd>\n"
                + "    </dl>\n"));
    }

    @Test
    public void shouldWriteSimplePojoWithStyleSheetWithIntegrity() throws IOException {
        @Data
        @HtmlStyleSheet(value = "/foo.css", integrity = "abc")
        class EmptyPojo {}

        String html = write(new EmptyPojo());

        assertThat(html).isEqualTo(head("Empty Pojo", ""
                + "    <link rel=\"stylesheet\" href=\"http://example.org/foo.css\" "
                + /*   */ "integrity=\"abc\" "
                + /*   */ "crossorigin=\"anonymous\"/>\n")
                + body(""));
    }

    @Test
    public void shouldWriteEmptyListOfSimplePojos() {
        @Data
        @AllArgsConstructor
        class SimplePojo {
            String one, two;
        }

        String html = write(new GenericEntity<List<SimplePojo>>(emptyList()) {});

        assertThat(html).isEqualTo(html("Simple Pojos", ""));
    }

    @Test
    public void shouldWriteListOfSimplePojos() {
        @Data
        @AllArgsConstructor
        class SimplePojo {
            String one, two;

            @Override public String toString() {
                return "«" + one + ":" + two + "»";
            }
        }

        String html = write(new GenericEntity<List<SimplePojo>>(
                asList(new SimplePojo("1", "a"), new SimplePojo("2", "b"))) {});

        assertThat(html).isEqualTo(html("Simple Pojos", ""
                + "    <ul>\n"
                + "      <li>«1:a»</li>\n"
                + "      <li>«2:b»</li>\n"
                + "    </ul>\n"
        ));
    }

    @Test
    public void shouldWriteListOfPojosWithSingularTitle() {
        @Value
        @HtmlTitle("HelloWorld!")
        class WorldPojo {
            String value;
        }

        assertThat(write(new GenericEntity<List<WorldPojo>>(asList(new WorldPojo("a"), new WorldPojo("b"))) {}))
                .isEqualTo(html("HelloWorld!s", ""
                        + "    <ul>\n"
                        + "      <li>WorldPojo(value=a)</li>\n"
                        + "      <li>WorldPojo(value=b)</li>\n"
                        + "    </ul>\n"));
    }

    @Test
    public void shouldWriteListOfPojosWithPluralTitle() {
        @Value
        @HtmlTitle(plural = "HelloWorlds!")
        class WorldPojo {
            String value;
        }

        assertThat(write(new GenericEntity<List<WorldPojo>>(asList(new WorldPojo("a"), new WorldPojo("b"))) {}))
                .isEqualTo(html("HelloWorlds!", ""
                        + "    <ul>\n"
                        + "      <li>WorldPojo(value=a)</li>\n"
                        + "      <li>WorldPojo(value=b)</li>\n"
                        + "    </ul>\n"));
    }

    @Test
    public void shouldWriteListOfPojosWithSingularAndPluralTitle() {
        @Value
        @HtmlTitle(value = "HelloWorld!", plural = "HelloWorlds!")
        class WorldPojo {
            String value;
        }

        assertThat(write(new GenericEntity<List<WorldPojo>>(asList(new WorldPojo("a"), new WorldPojo("b"))) {}))
                .isEqualTo(html("HelloWorlds!", ""
                        + "    <ul>\n"
                        + "      <li>WorldPojo(value=a)</li>\n"
                        + "      <li>WorldPojo(value=b)</li>\n"
                        + "    </ul>\n"));
    }

    @Test
    public void shouldWritePojoWithPanel() {
        @Data
        @HtmlPanel
        class PanelPojo {
            String one = "a";
        }

        String html = write(new PanelPojo());

        assertThat(html).isEqualTo(html("Panel Pojo", ""
                + "    <div class=\"panel panel-default\">\n"
                + "      <div class=\"panel-body\">\n"
                + "        <dl class=\"dl-horizontal panel-pojo\">\n"
                + "          <dt class=\"panel-pojo\" id=\"one\">one</dt>\n"
                + "          <dd class=\"panel-pojo\" id=\"one\">a</dd>\n"
                + "        </dl>\n"
                + "      </div>\n"
                + "    </div>\n"));
    }

    @Test
    public void shouldWritePojoWithPanelAndTitle() {
        @Data
        @HtmlPanel
        @HtmlTitle
        class PanelTitlePojo {
            String one = "a";
        }

        String html = write(new PanelTitlePojo());

        assertThat(html).isEqualTo(html("Panel Title Pojo", ""
                + "    <div class=\"panel panel-default\">\n"
                + "      <div class=\"panel-heading\"><h1 class=\"panel-title\">Panel Title Pojo</h1></div>\n"
                + "      <div class=\"panel-body\">\n"
                + "        <dl class=\"dl-horizontal panel-title-pojo\">\n"
                + "          <dt class=\"panel-title-pojo\" id=\"one\">one</dt>\n"
                + "          <dd class=\"panel-title-pojo\" id=\"one\">a</dd>\n"
                + "        </dl>\n"
                + "      </div>\n"
                + "    </div>\n"));
    }

    @Test
    public void shouldWritePojoWithPanelAndSingularTitle() {
        @Data
        @HtmlPanel
        @HtmlTitle("Foo")
        class PanelPojo {
            String one = "a";
        }

        String html = write(new PanelPojo());

        assertThat(html).isEqualTo(html("Foo", ""
                + "    <div class=\"panel panel-default\">\n"
                + "      <div class=\"panel-heading\"><h1 class=\"panel-title\">Foo</h1></div>\n"
                + "      <div class=\"panel-body\">\n"
                + "        <dl class=\"dl-horizontal panel-pojo\">\n"
                + "          <dt class=\"panel-pojo\" id=\"one\">one</dt>\n"
                + "          <dd class=\"panel-pojo\" id=\"one\">a</dd>\n"
                + "        </dl>\n"
                + "      </div>\n"
                + "    </div>\n"));
    }

    @Test
    public void shouldWritePojoWithPanelAndPluralTitle() {
        @Data
        @HtmlPanel
        @HtmlTitle(plural = "Foosball")
        class PanelPojo {
            String one = "a";
        }

        String html = write(new PanelPojo());

        assertThat(html).isEqualTo(html("Panel Pojo", ""
                + "    <div class=\"panel panel-default\">\n"
                + "      <div class=\"panel-heading\"><h1 class=\"panel-title\">Panel Pojo</h1></div>\n"
                + "      <div class=\"panel-body\">\n"
                + "        <dl class=\"dl-horizontal panel-pojo\">\n"
                + "          <dt class=\"panel-pojo\" id=\"one\">one</dt>\n"
                + "          <dd class=\"panel-pojo\" id=\"one\">a</dd>\n"
                + "        </dl>\n"
                + "      </div>\n"
                + "    </div>\n"));
    }

    @Test
    public void shouldWritePojoWithPanelAndSingularAndPluralTitle() {
        @Data
        @HtmlPanel
        @HtmlTitle(value = "Foo", plural = "Foosball")
        class PanelPojo {
            String one = "a";
        }

        String html = write(new PanelPojo());

        assertThat(html).isEqualTo(html("Foo", ""
                + "    <div class=\"panel panel-default\">\n"
                + "      <div class=\"panel-heading\"><h1 class=\"panel-title\">Foo</h1></div>\n"
                + "      <div class=\"panel-body\">\n"
                + "        <dl class=\"dl-horizontal panel-pojo\">\n"
                + "          <dt class=\"panel-pojo\" id=\"one\">one</dt>\n"
                + "          <dd class=\"panel-pojo\" id=\"one\">a</dd>\n"
                + "        </dl>\n"
                + "      </div>\n"
                + "    </div>\n"));
    }

    @Test
    public void shouldWritePojoWithTitleField() {
        @Data
        class PanelPojo {
            @HtmlTitle
            String one = "a";
        }

        String html = write(new PanelPojo());

        assertThat(html).isEqualTo(html("a", ""
                + "    <dl class=\"dl-horizontal panel-pojo\">\n"
                + "      <dt class=\"panel-pojo\" id=\"one\">one</dt>\n"
                + "      <dd class=\"panel-pojo\" id=\"one\">a</dd>\n"
                + "    </dl>\n"));
    }

    @Test
    public void shouldWritePojoWithTitleFieldAndPanel() {
        @Value
        @HtmlPanel
        class PanelPojo {
            @HtmlTitle
            String one;
        }

        String html = write(new PanelPojo("a"));

        writeToFile(html);
        assertThat(html).isEqualTo(html("a", ""
                + "    <div class=\"panel panel-default\">\n"
                + "      <div class=\"panel-heading\"><h1 class=\"panel-title\">a</h1></div>\n"
                + "      <div class=\"panel-body\">\n"
                + "        <dl class=\"dl-horizontal panel-pojo\">\n"
                + "          <dt class=\"panel-pojo\" id=\"one\">one</dt>\n"
                + "          <dd class=\"panel-pojo\" id=\"one\">a</dd>\n"
                + "        </dl>\n"
                + "      </div>\n"
                + "    </div>\n"));
    }

    @Test
    public void shouldWriteListOfPojosWithTitleFieldAndPanel() {
        @Value
        @HtmlPanel
        class PanelPojo {
            @HtmlTitle
            String one;
        }

        String html = write(new GenericEntity<List<PanelPojo>>(
                asList(new PanelPojo("a"), new PanelPojo("b"))) {});

        writeToFile(html);
        assertThat(html).isEqualTo(html("Panel Pojos", ""
                + "    <div class=\"panel panel-default\">\n"
                + "      <div class=\"panel-body\">\n"
                + "        <ul>\n"
                + "          <li>a</li>\n"
                + "          <li>b</li>\n"
                + "        </ul>\n"
                + "      </div>\n"
                + "    </div>\n"));
    }

    @Test
    public void shouldWriteListOfPojosWithPanel() {
        @Value
        @HtmlPanel
        class PanelPojo {
            String one;
        }

        String html = write(new GenericEntity<List<PanelPojo>>(
                asList(new PanelPojo("a"), new PanelPojo("b"))) {});

        assertThat(html).isEqualTo(html("Panel Pojos", ""
                + "    <div class=\"panel panel-default\">\n"
                + "      <div class=\"panel-body\">\n"
                + "        <ul>\n"
                + "          <li>PanelPojo(one=a)</li>\n"
                + "          <li>PanelPojo(one=b)</li>\n"
                + "        </ul>\n"
                + "      </div>\n"
                + "    </div>\n"));
    }

    @Test
    public void shouldWriteListOfPojosWithTitleAndPanel() {
        @Value
        @HtmlPanel
        @HtmlTitle
        class PanelPojo {
            String one;
        }

        String html = write(new GenericEntity<List<PanelPojo>>(
                asList(new PanelPojo("a"), new PanelPojo("b"))) {});

        writeToFile(html);
        assertThat(html).isEqualTo(html("Panel Pojos", ""
                + "    <div class=\"panel panel-default\">\n"
                + "      <div class=\"panel-heading\"><h1 class=\"panel-title\">Panel Pojos</h1></div>\n"
                + "      <div class=\"panel-body\">\n"
                + "        <ul>\n"
                + "          <li>PanelPojo(one=a)</li>\n"
                + "          <li>PanelPojo(one=b)</li>\n"
                + "        </ul>\n"
                + "      </div>\n"
                + "    </div>\n"));
    }

    @Value
    @HtmlPanel
    @HtmlTitle(value = "Foo", plural = "Foosball")
    public static class Player {
        String name;
    }

    @Test
    public void shouldWriteListOfStaticPojosWithPanelAndPluralTitle() {
        String html = write(new GenericEntity<List<Player>>(
                asList(new Player("a"), new Player("b"))) {});

        writeToFile(html);
        assertThat(html).isEqualTo(html("Foosball", ""
                + "    <div class=\"panel panel-default\">\n"
                + "      <div class=\"panel-heading\"><h1 class=\"panel-title\">Foosball</h1></div>\n"
                + "      <div class=\"panel-body\">\n"
                + "        <ul>\n"
                + "          <li>HtmlMessageBodyWriterTest.Player(name=a)</li>\n"
                + "          <li>HtmlMessageBodyWriterTest.Player(name=b)</li>\n"
                + "        </ul>\n"
                + "      </div>\n"
                + "    </div>\n"));
    }

    @Test
    public void shouldWriteListOfPojosWithIdLinkWithHtml() {
        when(writer.uriInfo.getRequestUri()).thenReturn(URI.create("http://example.org/app/things.html"));
        @Value
        class PojoWithId {
            @Id int id;
            String value;
        }

        assertThat(write(new GenericEntity<List<PojoWithId>>(asList(
                new PojoWithId(0, "a"), new PojoWithId(1, "b"))) {}))
                .isEqualTo(html("Pojo With Ids", ""
                        + "    <ul>\n"
                        + "      <li><a href=\"http://example.org/app/things/0.html\">PojoWithId(id=0, value=a)</a></li>\n"
                        + "      <li><a href=\"http://example.org/app/things/1.html\">PojoWithId(id=1, value=b)</a></li>\n"
                        + "    </ul>\n"));
    }

    @Test
    public void shouldWriteListOfPojosWithKeyLinkWithHtml() {
        when(writer.uriInfo.getRequestUri()).thenReturn(URI.create("http://example.org/app/things.html"));
        @Value
        class PojoWithKey {
            @WebResourceKey String key;
            String value;
        }

        assertThat(write(new GenericEntity<List<PojoWithKey>>(asList(
                new PojoWithKey("a", "one"), new PojoWithKey("b", "two"))) {}))
                .isEqualTo(html("Pojo With Keys", ""
                        + "    <ul>\n"
                        + "      <li><a href=\"http://example.org/app/things/a.html\">PojoWithKey(key=a, value=one)</a></li>\n"
                        + "      <li><a href=\"http://example.org/app/things/b.html\">PojoWithKey(key=b, value=two)</a></li>\n"
                        + "    </ul>\n"));
    }

    @Test
    public void shouldWriteListOfPojosWithIdLinkWithoutHtml() {
        when(writer.uriInfo.getRequestUri()).thenReturn(URI.create("http://example.org/app/things"));
        @Value
        class PojoWithId {
            @Id int id;
            String value;
        }

        assertThat(write(new GenericEntity<List<PojoWithId>>(asList(
                new PojoWithId(0, "a"), new PojoWithId(1, "b"))) {}))
                .isEqualTo(html("Pojo With Ids", ""
                        + "    <ul>\n"
                        + "      <li><a href=\"http://example.org/app/things/0.html\">PojoWithId(id=0, value=a)</a></li>\n"
                        + "      <li><a href=\"http://example.org/app/things/1.html\">PojoWithId(id=1, value=b)</a></li>\n"
                        + "    </ul>\n"));
    }

    @Test
    public void shouldNotWriteIdField() {
        @Value
        class PojoWithId {
            @Id int id = 0;
            String one = "a", two = "b";
        }

        // TODO no nl between dt and dd and continue
        assertThat(write(new PojoWithId()))
                .isEqualTo(html("Pojo With Id", ""
                        + "    <dl class=\"dl-horizontal pojo-with-id\">\n"
                        + "      <dt class=\"pojo-with-id\" id=\"one\">one</dt>\n"
                        + "      <dd class=\"pojo-with-id\" id=\"one\">a</dd>\n"
                        + "\n"
                        + "      <dt class=\"pojo-with-id\" id=\"two\">two</dt>\n"
                        + "      <dd class=\"pojo-with-id\" id=\"two\">b</dd>\n"
                        + "    </dl>\n"));
    }

    @Test
    public void shouldNotWriteOnlyIdField() {
        @Value
        class PojoWithId {
            @Id int id = 0;
        }

        assertThat(write(new PojoWithId())).isEqualTo(html("Pojo With Id", ""));
    }

    @Test
    public void shouldNotWriteVersionField() {
        @Value
        class PojoWithId {
            @Version int version = 0;
            String one = "a", two = "b";
        }

        assertThat(write(new PojoWithId()))
                .isEqualTo(html("Pojo With Id", ""
                        + "    <dl class=\"dl-horizontal pojo-with-id\">\n"
                        + "      <dt class=\"pojo-with-id\" id=\"one\">one</dt>\n"
                        + "      <dd class=\"pojo-with-id\" id=\"one\">a</dd>\n"
                        + "\n"
                        + "      <dt class=\"pojo-with-id\" id=\"two\">two</dt>\n"
                        + "      <dd class=\"pojo-with-id\" id=\"two\">b</dd>\n"
                        + "    </dl>\n"));
    }

    @Test
    public void shouldNotWriteXmlTransientField_LastWithoutNewline() {
        @Value
        class PojoWithId {
            String one = "a";
            @XmlTransient String two = "b";
        }

        assertThat(write(new PojoWithId()))
                .isEqualTo(html("Pojo With Id", ""
                        + "    <dl class=\"dl-horizontal pojo-with-id\">\n"
                        + "      <dt class=\"pojo-with-id\" id=\"one\">one</dt>\n"
                        + "      <dd class=\"pojo-with-id\" id=\"one\">a</dd>\n"
                        + "    </dl>\n"));
    }

    @Test
    @Ignore
    public void shouldWriteNestedListOfSimplePojos() {
        @Data
        @AllArgsConstructor
        class SimplePojo {
            String one, two;

            @Override public String toString() {
                return "«" + one + ":" + two + "»";
            }
        }

        @Data
        @AllArgsConstructor
        class ContainerPojo {
            String name;
            List<SimplePojo> nested;

            @Override public String toString() {
                return "«" + name + ":" + nested + "»";
            }
        }

        String html = write(new GenericEntity<List<ContainerPojo>>(asList(
                new ContainerPojo("first", asList(new SimplePojo("1", "a"), new SimplePojo("2", "b"))),
                new ContainerPojo("second", emptyList()),
                new ContainerPojo("third", singletonList(new SimplePojo("3", "c")))
        )) {});

        assertThat(html).isEqualTo(html("Simple Pojos", ""
                + "    <ul>\n"
                + "      <li>«1:a»</li>\n"
                + "      <li>«2:b»</li>\n"
                + "    </ul>\n"
        ));
    }

    // TODO content panel with custom heading
    //+ "    <div class=\"panel panel-default\">\n"
    //+ "      <div class=\"panel-heading\">\n"
    //+ "        <h1>\n"
    //+ "          <a href=\"http://localhost:8080/deployer/deployments\" class=\"glyphicon glyphicon-menu-left\"></a>\n"
    //+ "          ping.war\n"
    //+ "        </h1>\n"
    //+ "      </div>\n"
    //+ "\n"
    //+ "      <div class=\"panel-body\">\n"

    // TODO custom action button
    //+ "        <div style=\"float: right\">\n"
    //+ "          <form method=\"POST\" id=\"undeploy\" action=\"http://localhost:8080/deployer/deployments/ping\">\n"
    //+ "            <input type=\"hidden\" name=\"contextRoot\" value=\"ping\"/>\n"
    //+ "            <input type=\"hidden\" name=\"checksum\" value=\"A07311CDF5DA72573E58D22A4B3CA6BB8428A538\"/>\n"
    //+ "            <input type=\"hidden\" name=\"action\" value=\"undeploy\"/>\n"
    //+ "          </form>\n"
    //+ "          <div role=\"group\" class=\"btn-group\">\n"
    //+ "            <button class=\"btn btn-sm btn-danger\" form=\"undeploy\" type=\"submit\">Undeploy</button>\n"
    //+ "          </div>\n"
    //+ "        </div>\n"
    //+ "\n"
    // ...
    //+ "    </div>\n"

    // TODO footer
    //+ "    <footer class=\"text-muted pull-right\">Principal: anonymous</footer>\n"
    //+ "\n"

    // TODO table
    //+ "      <table class=\"table\">\n"
    //+ "        <tr>\n"
    //+ "          <td><a href=\"http://localhost:8080/deployer/deployments/deployer\">deployer</a></td>\n"
    //+ "          <td>deployer.war</td>\n"
    //+ "          <td title=\"SHA-1: 14DACB29FBC1C0C71C53C30643B897814ABEFD1F\">error</td>\n"
    //+ "        </tr>\n"
    //+ "        <tr>\n"
    //+ "          <td><a href=\"http://localhost:8080/deployer/deployments/dipper\">dipper</a></td>\n"
    //+ "          <td>dipper.war</td>\n"
    //+ "          <td title=\"SHA-1: C9383B692A65F5E3B5ED7B2C46D42526B817BC43\">error</td>\n"
    //+ "        </tr>\n"

    //+ "        <tr>\n"
    //+ "          <td><a href=\"http://localhost:8080/deployer/deployments/ping\">ping</a></td>\n"
    //+ "          <td>ping.war</td>\n"
    //+ "          <td title=\"SHA-1: A07311CDF5DA72573E58D22A4B3CA6BB8428A538\">error</td>\n"
    //+ "        </tr>\n"
    //+ "        <tr>\n"
    //+ "          <td colspan=\"3\"><a href=\"http://localhost:8080/deployer/deployments/!\">+</a></td>\n"
    //+ "        </tr>\n"
    //+ "      </table>\n"

    // TODO navbar
    //+ "    <nav class=\"navbar navbar-default navbar-fixed-top\">\n"
    //+ "      <div class=\"container-fluid\">\n"
    //+ "        <div class=\"navbar-header\">\n"
    //+ "          <a class=\"navbar-brand\">Deployer</a>\n"
    //+ "        </div>\n"
    //+ "        <div id=\"navbar\" class=\"navbar-collapse collapse\">\n"
    //+ "          <ul class=\"nav navbar-nav navbar-right\">\n"
    //+ "            <li class=\"raml\">\n"
    //+ "              <a href=\"http://localhost:8080/deployer/doc/api-console.html\"><img src=\"http://localhost:8080/deployer/img/raml.png\"/></a>\n"
    //+ "            </li>\n"
    //+ "            <li class=\"deployments active\"><a href=\"http://localhost:8080/deployer/deployments\">Deployments</a></li>\n"
    //+ "            <li class=\"loggers\"><a href=\"http://localhost:8080/deployer/loggers\">Loggers</a></li>\n"
    //+ "            <li class=\"datasources\"><a href=\"http://localhost:8080/deployer/datasources\">Data-Sources</a></li>\n"
    //+ "            <li class=\"config\"><a href=\"http://localhost:8080/deployer/config\"><span class=\"glyphicon glyphicon-cog\"/></a></li>\n"
    //+ "          </ul>\n"
    //+ "        </div>\n"
    //+ "      </div>\n"
    //+ "    </nav>\n"
    //+ "\n"
}
