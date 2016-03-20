package com.github.t1.webresource.model;

import com.github.t1.webresource.annotations.*;
import com.github.t1.webresource.codec.HtmlTitle;
import lombok.*;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.*;

import static lombok.AccessLevel.*;

@Entity
@WebResource
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@NoArgsConstructor(access = PRIVATE)
@HtmlTitle(value = "Person", plural = "People")
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
        return (tags != null) && tags.remove(tag);
    }

    public boolean untag(String key) {
        if (tags == null)
            return false;
        for (Iterator<Tag> iter = tags.iterator(); iter.hasNext(); ) {
            Tag tag = iter.next();
            if (tag.getKey().equals(key)) {
                iter.remove();
                return true;
            }
        }
        return false;
    }

    @Override public String toString() {
        return first + " " + last;
    }
}
