package com.github.t1.webresource.model;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.xml.bind.annotation.adapters.XmlAdapter;

@Slf4j
public class TagKeyAdapter extends XmlAdapter<String, Tag> {
    private static final EntityManagerFactory entityManagerFactory = loadEntityManagerFactory();

    private static EntityManagerFactory loadEntityManagerFactory() {
        try {
            log.debug("loading entity manager factory");
            EntityManagerFactory factory = Persistence.createEntityManagerFactory("primary");
            log.debug("entity manager factory found: {}", factory);
            return factory;
        } catch (PersistenceException e) {
            log.warn("can't load entity manager factory: {}", e.getMessage());
            return null;
        }
    }

    private static class TagDummy extends Tag {
        private static final long serialVersionUID = 1L;

        public TagDummy(String key) {
            super(key, "dummy-description for " + key);
        }
    }

    @Override
    public Tag unmarshal(String key) {
        log.info("unmarshal {}", key);
        if (key == null)
            return null;
        return loadTag(key);
    }

    private Tag loadTag(String key) {
        if (entityManagerFactory == null) {
            log.info("no entity manager factory available... creating tag dummy");
            return new TagDummy(key);
        }
        try {
            EntityManager em = entityManagerFactory.createEntityManager(); // not AutoCloseable :(
            try {
                TypedQuery<Tag> query = em.createQuery("from Tag tag where tag.key = :key", Tag.class);
                query.setParameter("key", key);
                log.info("load tag {}", key);
                return queryTag(em, query);
            } finally {
                em.close();
            }
        } catch (RuntimeException e) {
            log.error("can't unmarshal " + key, e);
            throw e;
        }
    }

    private Tag queryTag(EntityManager em, TypedQuery<Tag> query) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        Tag result = query.getSingleResult();
        transaction.commit();
        return result;
    }

    @Override
    public String marshal(Tag tag) {
        return (tag == null) ? null : tag.getKey();
    }
}
