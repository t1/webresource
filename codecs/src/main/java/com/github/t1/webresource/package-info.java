/**
 * Some {@link javax.ws.rs.ext.MessageBodyReader}s/{@link javax.ws.rs.ext.MessageBodyWriter}s, esp. for
 * <code>text/html</code>. To use them, just drop this dependency into your project and let the JAX-RS content
 * negotiation handle the details.
 * <p/>
 * Note: Don't annotate your GET methods with {@link javax.ws.rs.Produces} et.al.; that's only useful if you want to
 * produce a different content for a content type. I see that very often, just because it makes it easier to test from
 * the browser. Use proper content negotiation instead, e.g. by mapping a suffix like ".html" to <code>text/html</code>
 * in your web.xml using resteasy.media.type.mappings. See the demo module for an example.
 * <p/>
 * Note: The classes in this package are <code>public</code>, but not part of an official API! They might be changed in
 * any future version without special notice.
 */
package com.github.t1.webresource;