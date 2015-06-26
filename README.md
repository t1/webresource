Add @WebResource to your @Entity to generate a standard REST binding.

This is similar to what EclipseLink provides (http://wiki.eclipse.org/EclipseLink/Release/2.4.0/JPA-RS/REST-API), but IMHO hides technology better (e.g. why should a client care about the name of the persistence unit).

TODO generator:
* BeanValidation
* Binding for DAOs
* Use stereotype-helper (as soon as it supports annotation processors), so class annotations are inherited to the fields.
* Linked/sub-resources.
* Request-quota, or 429 Too many requests.
* Queries by prepared statements.
* pagination
* HEAD
* OPTIONS
* TRACE (if it's necessary; see http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html)
* Timestamp as lastModified.
* Dynamic generation at runtime?

has similarities to http://isis.apache.org
TODO codec:
* Provide favicons: <link rel="icon" type="image/png" href="/images/icon.png">


[![Join the chat at https://gitter.im/t1/webresource](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/t1/webresource?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)