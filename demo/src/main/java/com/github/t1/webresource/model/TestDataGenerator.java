package com.github.t1.webresource.model;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.persistence.*;
import java.util.List;

@Slf4j
@Singleton
@Startup
public class TestDataGenerator {
    @PersistenceContext
    private EntityManager em;

    @PostConstruct public void load() {
        log.warn("loading test data");
        List<Person> persons = em.createQuery("from Person", Person.class).getResultList();
        if (persons.isEmpty()) {
            log.debug("no test data, yet");
            generate("Joe", "Doe");
            generate("Tim", "Tom");
            generate("Jon", "Jog");
        } else {
            log.debug("already have data: {}", persons);
        }
    }

    private void generate(String first, String last) {
        Person person = new Person(first, last);
        log.debug("persist {}", person);
        em.persist(person);
    }
}
