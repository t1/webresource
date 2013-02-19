package com.github.t1.webresource;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.*;

@Entity
@XmlRootElement
@WebResource
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class MotionAgendaItem extends AgendaItem {
    private static final long serialVersionUID = 1L;

    /** required by JAXB */
    @SuppressWarnings("deprecation")
    MotionAgendaItem() {
    }

    public MotionAgendaItem(String text, AgendaTopic topic) {
        super(text, topic);
    }
}
