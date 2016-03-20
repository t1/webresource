package com.github.t1.webresource;

import com.github.t1.webresource.annotations.WebResource;
import lombok.Data;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Serializable;

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
