/**
 * Some {@link javax.ws.rs.ext.MessageBodyReader}s/{@link javax.ws.rs.ext.MessageBodyWriter}s, esp. for
 * <code>text/html</code>. To use them, just drop this dependency into your project and let the JAX-RS content
 * negotiation handle the details. Note: Don't annotate your GET methods with {@link javax.ws.rs.Produces} et.al.;
 * that's only useful if you want to produce a different content for a content type.
 */
package com.github.t1.webresource;