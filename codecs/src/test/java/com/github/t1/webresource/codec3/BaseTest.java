package com.github.t1.webresource.codec3;

import lombok.*;
import org.junit.Test;

import javax.ws.rs.core.*;
import java.io.*;
import java.lang.annotation.Annotation;
import java.nio.file.*;

import static javax.ws.rs.core.MediaType.*;
import static org.assertj.core.api.Assertions.*;

public class BaseTest {
    HtmlMessageBodyWriter writer = new HtmlMessageBodyWriter();

    @SneakyThrows(IOException.class)
    private String write(Object object) {
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Class<?> type = object.getClass();
        writer.writeTo(object, type, type, new Annotation[0], TEXT_HTML_TYPE, headers, stream);
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
                + "    <link rel=\"stylesheet\" href=\"" + writer.bootstrapCssUri() + "\" \n"
                + "        integrity=\"" + writer.bootstrapCssIntegrity() + "\" \n"
                + "        crossorigin=\"anonymous\">\n"
                + "  </head>\n"
                + "  <body class=\"container-fluid\" style=\"padding-top: 70px\">\n"
                + body
                + "    <script src=\"" + writer.jqueryUri() + "\" \n"
                + "        integrity=\"" + writer.jqueryIntegrity() + "\" \n"
                + "        crossorigin=\"anonymous\"></script>\n"
                + "    <script src=\"" + writer.bootstrapJsUri() + "\" \n"
                + "        integrity=\"" + writer.bootstrapJsIntegrity() + "\" \n"
                + "        crossorigin=\"anonymous\"></script>\n"
                + "  </body>\n"
                + "</html>\n";
    }

    @Test
    public void shouldGenerateBasicHtml() {
        class EmptyPojo {}

        assertThat(write(new EmptyPojo())).isEqualTo(html("Empty Pojo", ""));
    }

    @Test
    public void shouldWriteSimplePojo() throws IOException {
        @Data
        class SimplePojo {
            String one = "a", two = "b";
            int three = 3;
        }

        String html = write(new SimplePojo());

        Files.write(Paths.get("target", "simple-pojo.html"), html.getBytes());

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

    // TODO content panel with default heading
    //+ "    <div class=\"panel panel-default\">\n"
    //+ "      <div class=\"panel-heading\"><h1>Deployments</h1></div>\n"
    //+ "\n"
    // ...
    //+ "    </div>\n"
    //+ "\n"

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
