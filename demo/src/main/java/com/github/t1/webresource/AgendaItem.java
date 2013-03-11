package com.github.t1.webresource;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

import lombok.Data;

@Entity
@XmlRootElement
@WebResource
@Data
public abstract class AgendaItem implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @XmlAttribute
    private BigDecimal id;
 
    @NotNull
    private String text;

    private AgendaTopic topic;

    // TODO responsible persons (RACI?)

    /** required by JAXB */
    @Deprecated
    AgendaItem() {
    }

    public AgendaItem(String text, AgendaTopic topic) {
        this.text = text;
        this.topic = topic;
    }
}
