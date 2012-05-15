package com.github.t1.webresource;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.*;

@Entity
@XmlRootElement
@WebResource
public class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Size(min = 1, max = 100)
    @Pattern(regexp = "\\p{Alpha}*", message = "must contain only alphabetical characters")
    private String first;

    @NotNull
    @Size(min = 1, max = 50)
    @Pattern(regexp = "\\p{Alpha}*", message = "must contain only alphabetical characters")
    private String last;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Tag> tags;

    /** required by JAXB */
    Person() {
    }

    public Person(String first, String last) {
        this.first = first;
        this.last = last;
    }

    @XmlAttribute
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public void addTag(Tag tag) {
        if (tags == null)
            tags = new ArrayList<>();
        tags.add(tag);
    }

    public boolean removeTag(Tag tag) {
        if (tags == null)
            return false;
        return tags.remove(tag);
    }

    @Override
    public String toString() {
        return "Person [" + (id != null ? "id=" + id + ", " : "") + (first != null ? "first=" + first + ", " : "")
                + (last != null ? "last=" + last + ", " : "") + (tags != null ? "tags=" + tags : "") + "]";
    }
}
