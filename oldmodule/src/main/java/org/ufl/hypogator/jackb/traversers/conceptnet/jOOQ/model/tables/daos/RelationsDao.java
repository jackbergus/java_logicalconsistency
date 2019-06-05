/*
 * This file is generated by jOOQ.
 */
package org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.tables.daos;


import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.tables.Relations;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.tables.records.RelationsRecord;

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
public class RelationsDao extends DAOImpl<RelationsRecord, org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.tables.pojos.Relations, Integer> {

    /**
     * Create a new RelationsDao without any configuration
     */
    public RelationsDao() {
        super(Relations.RELATIONS, org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.tables.pojos.Relations.class);
    }

    /**
     * Create a new RelationsDao with an attached configuration
     */
    public RelationsDao(Configuration configuration) {
        super(Relations.RELATIONS, org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.tables.pojos.Relations.class, configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Integer getId(org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.tables.pojos.Relations object) {
        return object.getId();
    }

    /**
     * Fetch records that have <code>id IN (values)</code>
     */
    public List<org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.tables.pojos.Relations> fetchById(Integer... values) {
        return fetch(Relations.RELATIONS.ID, values);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.tables.pojos.Relations fetchOneById(Integer value) {
        return fetchOne(Relations.RELATIONS.ID, value);
    }

    /**
     * Fetch records that have <code>uri IN (values)</code>
     */
    public List<org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.tables.pojos.Relations> fetchByUri(String... values) {
        return fetch(Relations.RELATIONS.URI, values);
    }

    /**
     * Fetch a unique record that has <code>uri = value</code>
     */
    public org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.tables.pojos.Relations fetchOneByUri(String value) {
        return fetchOne(Relations.RELATIONS.URI, value);
    }

    /**
     * Fetch records that have <code>directed IN (values)</code>
     */
    public List<org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.tables.pojos.Relations> fetchByDirected(Boolean... values) {
        return fetch(Relations.RELATIONS.DIRECTED, values);
    }
}