package com.github.t1.webresource;

import static org.junit.Assert.*;

import java.io.File;

import javax.ws.rs.client.*;
import javax.ws.rs.core.*;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class WebresourceIT {
    private static final Client CLIENT = null; // ClientFactory.newClient();
    private static final String WAR = "webresource-it";

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, WAR + ".war");
        war.addAsLibraries(new File("target/webresource-generator.jar"));
        war.addClasses(TestEntity.class);
        // war.addClass(TestEntity.class.getName() + "WebResource");
        return war;
    }

    @Test
    @Ignore("just an experiment")
    public void shouldHttpGet() throws Exception {
        WebTarget target = CLIENT.target("http://example.com/shop");
        Form form = new Form().param("customer", "Bill").param("product", "IPhone 5").param("CC", "4444 4444 4444 4444");
        Response response = target.request().post(Entity.form(form));
        assertEquals(200, response.getStatus());
        // Order order = response.readEntity(Order.class);
    }

    @Test
    @Ignore("just an experiment")
    public void shouldConvertXmlSuffixToMediaType() throws Exception {
        WebTarget target = CLIENT.target("http://example.com/" + WAR + "/testentity.xml");
        Response response = target.request().get();
        assertEquals(200, response.getStatus());
        assertEquals(MediaType.APPLICATION_XML_TYPE, response.getMediaType());
        Object entity = response.getEntity();
        System.out.println(entity.getClass() + ": " + entity);
    }
}
