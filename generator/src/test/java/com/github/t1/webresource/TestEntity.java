package com.github.t1.webresource;

import java.io.Serializable;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@Entity
@WebResource
@XmlRootElement
public class TestEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @XmlAttribute
    private Long id;

    private String text;
}
