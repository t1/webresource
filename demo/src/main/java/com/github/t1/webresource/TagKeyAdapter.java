package com.github.t1.webresource;

import javax.persistence.*;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TagKeyAdapter extends XmlAdapter<String, Tag> {
    static EntityManagerFactory entityManagerFactory;

    static {
        try {
            log.debug("loading entity manager factory");
            entityManagerFactory = Persistence.createEntityManagerFactory("primary");
            log.debug("entity manager factory found: {}", entityManagerFactory);
        } catch (PersistenceException e) {
            log.warn("can't load entity manager factory: {}", e.getMessage());
        }
    }

    private static class TagDummy extends Tag {
        private static final long serialVersionUID = 1L;

        public TagDummy(String key) {
            super(key, "dummy-description for " + key);
        }
    }

    @Override
    public Tag unmarshal(String key) throws Exception {
        log.info("unmarshal {}", key);
        if (key == null)
            return null;
        if (entityManagerFactory == null) {
            log.info("no entity manager factory available... creating tag dummy");
            return new TagDummy(key);
        }
        return loadTag(key);
    }

    private Tag loadTag(String key) {
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
    public String marshal(Tag tag) throws Exception {
        return (tag == null) ? null : tag.getKey();
    }
}
