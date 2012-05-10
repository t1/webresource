package com.github.t1.webresource;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.*;

import com.github.t1.webresource.WebResource;

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((first == null) ? 0 : first.hashCode());
        result = prime * result + ((last == null) ? 0 : last.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Person other = (Person) obj;
        if (first == null) {
            if (other.first != null)
                return false;
        } else if (!first.equals(other.first))
            return false;
        if (last == null) {
            if (other.last != null)
                return false;
        } else if (!last.equals(other.last))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Person [first=" + first + ", last=" + last + "]";
    }
}
