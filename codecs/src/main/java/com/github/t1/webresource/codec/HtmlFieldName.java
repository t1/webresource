package com.github.t1.webresource.codec;

import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;

import javax.xml.bind.annotation.*;

/**
 * The name of the label for input fields or columns in tables. Defaults to the name of the trait; for
 * {@link XmlRootElement}s this is the {@link XmlElementWrapper#name()}, or (if there is none) the
 * {@link XmlElement#name()}/ {@link XmlAttribute#name()}, or (if there is none) the name of the pojo property.
 */
@Retention(RUNTIME)
public @interface HtmlFieldName {
    String value();
}
