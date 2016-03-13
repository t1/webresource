package com.github.t1.webresource.codec;

import lombok.*;
import org.junit.Test;

import javax.ws.rs.core.*;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.List;

import static java.util.Arrays.*;
import static javax.ws.rs.core.MediaType.*;
import static org.assertj.core.api.Assertions.*;

public class BaseTest {
    HtmlMessageBodyWriter writer = new HtmlMessageBodyWriter();

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
                + "  </head>\n"
                + "  <body class=\"container-fluid\" style=\"padding-top: 70px\">\n"
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

        assertThat(write(new EmptyPojo())).isEqualTo(html("Empty Pojo", ""));
    }

    @Test
    public void shouldWriteEmptyPojoWithTitle() {
        @HtmlTitle("HelloWorld!")
        class EmptyPojo {}

        assertThat(write(new EmptyPojo())).isEqualTo(html("HelloWorld!", ""));
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
                + "    <dl class=\"dl-horizontal\">\n"
                + "      <dt>one</dt>\n"
                + "      <dd>a</dd>\n"
                + "\n"
                + "      <dt>two</dt>\n"
                + "      <dd>b</dd>\n"
                + "\n"
                + "      <dt>three</dt>\n"
                + "      <dd>3</dd>\n"
                + "    </dl>\n"));
    }

    @Test
    public void shouldWriteListOfSimplePojos() {
        @Data
        @AllArgsConstructor
        class SimplePojo {
            String one, two;

            public String toString() {
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
                + "      <div class=\"panel-heading\"><h1>Panel Pojo</h1></div>\n"
                + "      <dl class=\"dl-horizontal\">\n"
                + "        <dt>one</dt>\n"
                + "        <dd>a</dd>\n"
                + "      </dl>\n"
                + "    </div>\n"));
    }

    @Test
    public void shouldWritePojoWithTitleAndPanel() {
        @Data
        @HtmlPanel
        @HtmlTitle("Foo")
        class PanelPojo {
            String one = "a";
        }

        String html = write(new PanelPojo());

        writeToFile(html);
        assertThat(html).isEqualTo(html("Foo", ""
                + "    <div class=\"panel panel-default\">\n"
                + "      <div class=\"panel-heading\"><h1>Foo</h1></div>\n"
                + "      <dl class=\"dl-horizontal\">\n"
                + "        <dt>one</dt>\n"
                + "        <dd>a</dd>\n"
                + "      </dl>\n"
                + "    </div>\n"));
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
