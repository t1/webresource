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

    @PostConstruct public void init() {
        log.warn("loading test data");
        List<Person> persons = em.createQuery("from Person", Person.class).getResultList();
        if (persons.isEmpty()) {
            log.debug("no test data, yet");
            generateTestData();
        } else {
            log.debug("already have data: {}", persons);
        }
    }

    private void generateTestData() {
        generatePersons();
        generateGroups();
    }

    private void generatePersons() {
        generatePerson("Joe", "Doe");
        generatePerson("Tim", "Tom");
        generatePerson("Jon", "Jog");
    }

    private void generatePerson(String first, String last) {
        Person person = new Person(first, last);
        log.debug("persist person: {}", person);
        em.persist(person);
    }

    private void generateGroups() {
        generateGroup("Teachers");
        generateGroup("Students");
        generateGroup("Parents");
    }

    private void generateGroup(String name) {
        Group group = new Group();
        group.setName(name);
        group.setKey(name.toLowerCase());
        group.setDescription("These are the " + name.toLowerCase() + "s.");
        log.debug("persist group: {}", group);
        em.persist(group);
    }
}
