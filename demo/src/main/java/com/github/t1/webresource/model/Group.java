package com.github.t1.webresource.model;

import java.io.Serializable;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import com.github.t1.webresource.WebResource;

import lombok.*;

@Entity(name = "group_")
@WebResource
// JAXB
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
// lombok
@Getter
@Setter
@ToString
public class Group implements Serializable {
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
}
