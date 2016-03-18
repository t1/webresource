package com.github.t1.webresource.model;

import com.github.t1.webresource.codec.HtmlMessageBodyWriter;
import lombok.SneakyThrows;
import org.junit.Test;

import javax.ws.rs.core.*;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import static java.util.Arrays.*;
import static javax.ws.rs.core.MediaType.*;
import static org.assertj.core.api.Assertions.*;

public class TestTest {
    private final HtmlMessageBodyWriter writer = new HtmlMessageBodyWriter();

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

    @Test
    public void shouldWriteListOfPersons() {
        String html = write(new GenericEntity<List<Person>>(asList(
                new Person("Joe", "Doe"),
                new Person("Tim", "Tom").tag(new Tag("t", "tt"))
        )) {});

        assertThat(html).isEqualTo(""
                + "<!DOCTYPE html>\n"
                + "<html>\n"
                + "  <head>\n"
                + "    <meta charset=\"utf-8\"/>\n"
                + "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"/>\n"
                + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/>\n"
                + "\n"
                + "    <title>People</title>\n"
                + "\n"
                + "    <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css\" integrity=\"sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7\" crossorigin=\"anonymous\"/>\n"
                + "  </head>\n"
                + "  <body class=\"container-fluid\" style=\"padding-top: 15px\">\n"
                + "    <div class=\"panel panel-default\">\n"
                + "      <div class=\"panel-heading\"><h1>People</h1></div>\n"
                + "      <div class=\"panel-body\">\n"
                + "        <ul>\n"
                + "          <li>Joe Doe</li>\n"
                + "          <li>Tim Tom</li>\n"
                + "        </ul>\n"
                + "      </div>\n"
                + "    </div>\n"
                + "    <script src=\"https://code.jquery.com/jquery-2.2.1.min.js\" integrity=\"sha384-8C+3bW/ArbXinsJduAjm9O7WNnuOcO+Bok/VScRYikawtvz4ZPrpXtGfKIewM9dK\" crossorigin=\"anonymous\"></script>\n"
                + "    <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js\" integrity=\"sha384-0mSbJDEHialfmuBBQP6A4Qrprq5OVfW37PRR3j5ELqxss1yVqOtnepnHVP9aJ7xS\" crossorigin=\"anonymous\"></script>\n"
                + "  </body>\n"
                + "</html>\n"
                + "");
    }
}
