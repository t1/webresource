package com.github.t1.webresource;

import java.io.*;

import javax.ws.rs.*;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;

public class PersonWebClient {
    private static final String BASE_URL = "http://localhost:8080/webresource-demo/";

    public interface PersonService {
        @GET
        @Path("persons")
        @Produces("text/xml")
        String getPersons();
    }

    public static void main(String[] args) throws IOException {
        // RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
        // SimpleClient client = ProxyFactory.create(SimpleClient.class, "http://localhost:8081");
        // client.putBasic("hello world");
        new PersonWebClient().run();
    }

    private final HttpClient httpClient = new DefaultHttpClient();
    private final boolean verbose = false;

    public void run() throws IOException {
        get("persons");
        String created = create();
        get("persons");
        update(created);
        get(created);
        delete(created);
        get("persons");
    }

    private void get(String relativePath) throws IOException, ClientProtocolException {
        System.out.println("GET " + relativePath);
        HttpGet request = new HttpGet(BASE_URL + relativePath);
        setAcceptHeader(request);

        printResponse(httpClient.execute(request));
    }

    private void setAcceptHeader(HttpMessage request) {
        request.setHeader(new BasicHeader("Accept", "text/xml"));
    }

    private String create() throws IOException, ClientProtocolException {
        System.out.println("POST");
        HttpPost request = new HttpPost(BASE_URL + "persons");
        setEntity(request, "" //
                + "<person>" //
                + "  <first>Joe</first>" //
                + "  <last>Doe</last>" //
                + "</person>");

        HttpResponse response = httpClient.execute(request);
        response.getEntity().getContent().close(); // ignore

        String location = response.getFirstHeader("Location").getValue();
        assert location.startsWith(BASE_URL);
        System.out.println("created: " + location + "\n");
        return location.substring(BASE_URL.length());
    }

    protected void setEntity(HttpEntityEnclosingRequest request, final String body) {
        request.setHeader(new BasicHeader("Content-Type", "text/xml"));
        ContentProducer cp = new ContentProducer() {
            @Override
            public void writeTo(OutputStream outstream) throws IOException {
                Writer writer = new OutputStreamWriter(outstream, "UTF-8");
                writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                writer.write(body);
                writer.flush();
            }
        };
        HttpEntity entity = new EntityTemplate(cp);
        request.setEntity(entity);
    }

    private void update(String relativePath) throws IOException {
        System.out.println("PUT " + relativePath);
        HttpPut request = new HttpPut(BASE_URL + relativePath);
        setEntity(request, "" //
                + "<person>" //
                + "  <first>Jim</first>" //
                + "  <last>Doe</last>" //
                + "</person>");
        setAcceptHeader(request);

        printResponse(httpClient.execute(request));
    }

    private void delete(String relativePath) throws IOException {
        System.out.println("DELETE " + relativePath);
        HttpDelete request = new HttpDelete(BASE_URL + relativePath);
        setAcceptHeader(request);

        printResponse(httpClient.execute(request));
    }

    private void printResponse(HttpResponse response) throws IOException {
        if (verbose) {
            System.out.println(response.getStatusLine());
            for (Header header : response.getAllHeaders()) {
                System.out.println(header);
            }
        }
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
            while (true) {
                String line = reader.readLine();
                if (line == null)
                    break;
                System.out.println(line);
            }
        }
        System.out.println();
    }
}
