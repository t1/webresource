package com.github.t1.webresource;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.*;

import lombok.Data;

@Entity
@XmlRootElement
@WebResource
@Data
public class Meeting implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @XmlAttribute
    private Long id;

    @NotNull
    @Size(min = 1, max = 100)
    private String title;

    @NotNull
    private Date begin;

    // TODO series
    // TODO duration/end; date-of-writing
    // TODO attendees, absentees, distribution

    /** required by JAXB */
    Meeting() {
    }

    public Meeting(String title, Date begin) {
        this.title = title;
        this.begin = begin;
    }
}
