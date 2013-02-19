package com.github.t1.webresource;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.*;

@Entity
@XmlRootElement
@WebResource
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class InformationAgendaItem extends AgendaItem {
    private static final long serialVersionUID = 1L;

    /** required by JAXB */
    @SuppressWarnings("deprecation")
    InformationAgendaItem() {
    }

    public InformationAgendaItem(String text, AgendaTopic topic) {
        super(text, topic);
    }
}
