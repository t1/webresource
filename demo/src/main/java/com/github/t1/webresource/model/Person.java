package com.github.t1.webresource.model;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import lombok.*;

import com.github.t1.webresource.*;

@Entity
@WebResource
// JAXB
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
// lombok
@Getter
@Setter
@ToString
@HtmlStyleSheet("stylesheets/main.css")
public class Person implements Serializable {
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
    String first;

    private @Column
    String last;

    @WebSubResource
    @XmlElement(name = "tag")
    @XmlElementWrapper(name = "tags")
    private @ManyToMany(fetch = FetchType.EAGER)
    Set<Tag> tags;

    @WebSubResource
    private @ManyToOne
    Category category;

    @WebSubResource
    @XmlElement(name = "group")
    @XmlElementWrapper(name = "groups")
    private @ManyToMany(fetch = FetchType.EAGER)
    Set<Group> groups;

    /** @deprecated required by JAXB and JPA */
    @Deprecated
    Person() {}

    public Person(String first, String last) {
        this.first = first;
        this.last = last;
    }

    public Set<Tag> getTags() {
        if (tags == null)
            tags = new HashSet<>();
        return tags;
    }

    public Person tag(Tag tag) {
        if (tags == null)
            tags = new HashSet<>();
        tags.add(tag);
        return this;
    }

    public boolean untag(Tag tag) {
        if (tags == null)
            return false;
        return tags.remove(tag);
    }

    public boolean untag(String key) {
        if (tags == null)
            return false;
        for (Iterator<Tag> iter = tags.iterator(); iter.hasNext();) {
            Tag tag = iter.next();
            if (tag.getKey().equals(key)) {
                iter.remove();
                return true;
            }
        }
        return false;
    }
}
