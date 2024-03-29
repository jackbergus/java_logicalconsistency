/*
 * This file is generated by jOOQ.
 */
package org.ufl.aida.ta2.tables.daos;


import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;
import org.ufl.aida.ta2.tables.Tuples;
import org.ufl.aida.ta2.tables.records.TuplesRecord;


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
public class TuplesDao extends DAOImpl<TuplesRecord, org.ufl.aida.ta2.tables.pojos.Tuples, String> {

    /**
     * Create a new TuplesDao without any configuration
     */
    public TuplesDao() {
        super(Tuples.TUPLES, org.ufl.aida.ta2.tables.pojos.Tuples.class);
    }

    /**
     * Create a new TuplesDao with an attached configuration
     */
    public TuplesDao(Configuration configuration) {
        super(Tuples.TUPLES, org.ufl.aida.ta2.tables.pojos.Tuples.class, configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getId(org.ufl.aida.ta2.tables.pojos.Tuples object) {
        return object.getMid();
    }

    /**
     * Fetch records that have <code>nistType IN (values)</code>
     */
    public List<org.ufl.aida.ta2.tables.pojos.Tuples> fetchByNisttype(String... values) {
        return fetch(Tuples.TUPLES.NISTTYPE, values);
    }

    /**
     * Fetch records that have <code>mid IN (values)</code>
     */
    public List<org.ufl.aida.ta2.tables.pojos.Tuples> fetchByMid(String... values) {
        return fetch(Tuples.TUPLES.MID, values);
    }

    /**
     * Fetch a unique record that has <code>mid = value</code>
     */
    public org.ufl.aida.ta2.tables.pojos.Tuples fetchOneByMid(String value) {
        return fetchOne(Tuples.TUPLES.MID, value);
    }

    /**
     * Fetch records that have <code>score IN (values)</code>
     */
    public List<org.ufl.aida.ta2.tables.pojos.Tuples> fetchByScore(Double... values) {
        return fetch(Tuples.TUPLES.SCORE, values);
    }

    /**
     * Fetch records that have <code>negated IN (values)</code>
     */
    public List<org.ufl.aida.ta2.tables.pojos.Tuples> fetchByNegated(Boolean... values) {
        return fetch(Tuples.TUPLES.NEGATED, values);
    }

    /**
     * Fetch records that have <code>hedged IN (values)</code>
     */
    public List<org.ufl.aida.ta2.tables.pojos.Tuples> fetchByHedged(Boolean... values) {
        return fetch(Tuples.TUPLES.HEDGED, values);
    }

    /**
     * Fetch records that have <code>constituent IN (values)</code>
     */
    public List<org.ufl.aida.ta2.tables.pojos.Tuples> fetchByConstituent(String[]... values) {
        return fetch(Tuples.TUPLES.CONSTITUENT, values);
    }

    /**
     * Fetch records that have <code>array_agg IN (values)</code>
     */
    public List<org.ufl.aida.ta2.tables.pojos.Tuples> fetchByArrayAgg(Object[]... values) {
        return fetch(Tuples.TUPLES.ARRAY_AGG, values);
    }
}
