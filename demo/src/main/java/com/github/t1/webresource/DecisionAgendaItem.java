package com.github.t1.webresource;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.*;

@Entity
@XmlRootElement
@WebResource
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class DecisionAgendaItem extends AgendaItem {
    private static final long serialVersionUID = 1L;

    /** required by JAXB */
    @SuppressWarnings("deprecation")
    DecisionAgendaItem() {
    }

    public DecisionAgendaItem(String text, AgendaTopic topic) {
        super(text, topic);
    }
}
