package com.github.t1.webresource;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

import lombok.Data;

@Entity
@XmlRootElement
@WebResource
@Data
public abstract class AgendaTopic implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @XmlAttribute
    private Long id;

    @NotNull
    private String text;

    @NotNull
    private Meeting meeting;

    /** required by JAXB */
    @Deprecated
    AgendaTopic() {
    }

    public AgendaTopic(String text, Meeting meeting) {
        this.text = text;
        this.meeting = meeting;
    }
}
