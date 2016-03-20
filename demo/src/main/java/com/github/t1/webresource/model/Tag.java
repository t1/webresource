package com.github.t1.webresource.model;

import com.github.t1.webresource.annotations.*;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Serializable;

import static lombok.AccessLevel.*;

@Entity
@WebResource
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@lombok.Data
@NoArgsConstructor(access = PRIVATE)
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

    public Tag(String key, String description) {
        this.key = key;
        this.description = description;
    }
}
