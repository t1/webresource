package com.github.t1.webresource;

import java.io.Serializable;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

@Entity
@WebResource
// JAXB
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
// lombok
@lombok.Data
public class Tag implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlTransient
    private @Id
    @GeneratedValue
    Long id;

    @XmlTransient
    private @Column
    @Version
    int version = 0;

    @XmlAttribute
    private @WebResourceKey
    String key;

    @XmlValue
    private @Column
    @WebSubResource
    String description;

    /** @deprecated required by JAXB and JPA */
    @Deprecated
    Tag() {}

    public Tag(String key, String description) {
        this.key = key;
        this.description = description;
    }
}
