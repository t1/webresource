package com.github.t1.webresource;

import java.io.Serializable;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import lombok.*;

@Entity
@WebResource
// JAXB
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
// lombok
@Getter
@Setter
@ToString
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlTransient
    private @Id
    @GeneratedValue
    Long id;

    @XmlTransient
    private @Column
    @Version
    int version;

    private @Column
    String name;

    private @Column
    String description;

    /** @deprecated required by JAXB */
    @Deprecated
    Category() {}

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
