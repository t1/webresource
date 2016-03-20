package com.github.t1.webresource.model;

import com.github.t1.webresource.annotations.*;
import com.github.t1.webresource.codec.HtmlTitle;
import lombok.*;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Serializable;

@Entity(name = "group_")
@WebResource
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@HtmlTitle
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

    @WebResourceKey
    private @Column
    String key;

    @HtmlTitle
    private @Column
    String name;

    private @Column
    String description;

    @Override public String toString() {
        return "Group[" + key + ":" + name + ":" + description + "]";
    }
}
