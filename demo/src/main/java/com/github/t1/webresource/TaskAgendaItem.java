package com.github.t1.webresource;

import java.util.Date;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.*;

@Entity
@XmlRootElement
@WebResource
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class TaskAgendaItem extends AgendaItem {
    private static final long serialVersionUID = 1L;

    @NotNull
    private Date due;

    /** required by JAXB */
    @SuppressWarnings("deprecation")
    TaskAgendaItem() {
    }

    public TaskAgendaItem(String text, AgendaTopic topic, Date due) {
        super(text, topic);
        this.due = due;
    }
}
