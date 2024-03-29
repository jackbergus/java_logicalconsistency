/*
 * This file is generated by jOOQ.
 */
package org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.tables.daos;


import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.tables.Sources;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.tables.records.SourcesRecord;

import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.3"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SourcesDao extends DAOImpl<SourcesRecord, org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.tables.pojos.Sources, Integer> {

    /**
     * Create a new SourcesDao without any configuration
     */
    public SourcesDao() {
        super(Sources.SOURCES, org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.tables.pojos.Sources.class);
    }

    /**
     * Create a new SourcesDao with an attached configuration
     */
    public SourcesDao(Configuration configuration) {
        super(Sources.SOURCES, org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.tables.pojos.Sources.class, configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Integer getId(org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.tables.pojos.Sources object) {
        return object.getId();
    }

    /**
     * Fetch records that have <code>id IN (values)</code>
     */
    public List<org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.tables.pojos.Sources> fetchById(Integer... values) {
        return fetch(Sources.SOURCES.ID, values);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.tables.pojos.Sources fetchOneById(Integer value) {
        return fetchOne(Sources.SOURCES.ID, value);
    }

    /**
     * Fetch records that have <code>uri IN (values)</code>
     */
    public List<org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.tables.pojos.Sources> fetchByUri(String... values) {
        return fetch(Sources.SOURCES.URI, values);
    }

    /**
     * Fetch a unique record that has <code>uri = value</code>
     */
    public org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.tables.pojos.Sources fetchOneByUri(String value) {
        return fetchOne(Sources.SOURCES.URI, value);
    }
}
