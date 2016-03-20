package com.github.t1.webresource.model;

import com.github.t1.webresource.annotations.WebResource;
import lombok.*;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Serializable;

import static lombok.AccessLevel.*;

@Entity
@WebResource
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@ToString
@NoArgsConstructor(access = PRIVATE)
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

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
