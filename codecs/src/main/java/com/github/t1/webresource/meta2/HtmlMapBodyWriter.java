package com.github.t1.webresource.meta2;

import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

@javax.ws.rs.ext.Provider
@javax.ws.rs.Produces("text/html")
public class HtmlMapBodyWriter extends AbstractHtmlMessageBodyWriter<Map<?, ?>> {
    @Inject
    private MetaDataStore metaData;

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Map.class.isAssignableFrom(type);
    }

    @Override
    protected void printBody(Map<?, ?> map, PrintWriter out) {
        out.println("<table>");
        MapMetaData meta = metaData.get(map);
        printHeader(meta, out);
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            out.print("<tr><td>");
            printItem(entry.getKey(), out);
            out.print("</td><td>");
            printItem(entry.getValue(), out);
            out.println("</td></tr>");
        }
        out.println("</table>");
    }

    private void printHeader(MapMetaData meta, PrintWriter out) {
        String keyTitle = (meta == null) ? "Key" : meta.keyTitle();
        String valueTitle = (meta == null) ? "Value" : meta.valueTitle();
        out.append("<tr><td>").append(keyTitle).append("</td><td>").append(valueTitle).println("</td></tr>");
    }

    @Override
    protected String title(Map<?, ?> map) {
        MapMetaData meta = metaData.get(map);
        return (meta == null) ? null : meta.pageTitle();
    }
}
