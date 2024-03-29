/*
 * This file is generated by jOOQ.
 */
package org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.tables.records;


import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.tables.Relations;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;


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
public class RelationsRecord extends UpdatableRecordImpl<RelationsRecord> implements Record3<Integer, String, Boolean> {

    private static final long serialVersionUID = 2059962505;

    /**
     * Setter for <code>public.relations.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.relations.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>public.relations.uri</code>.
     */
    public void setUri(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.relations.uri</code>.
     */
    public String getUri() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.relations.directed</code>.
     */
    public void setDirected(Boolean value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.relations.directed</code>.
     */
    public Boolean getDirected() {
        return (Boolean) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<Integer, String, Boolean> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<Integer, String, Boolean> valuesRow() {
        return (Row3) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return Relations.RELATIONS.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return Relations.RELATIONS.URI;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field3() {
        return Relations.RELATIONS.DIRECTED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getUri();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean component3() {
        return getDirected();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getUri();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value3() {
        return getDirected();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RelationsRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RelationsRecord value2(String value) {
        setUri(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RelationsRecord value3(Boolean value) {
        setDirected(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RelationsRecord values(Integer value1, String value2, Boolean value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached RelationsRecord
     */
    public RelationsRecord() {
        super(Relations.RELATIONS);
    }

    /**
     * Create a detached, initialised RelationsRecord
     */
    public RelationsRecord(Integer id, String uri, Boolean directed) {
        super(Relations.RELATIONS);

        set(0, id);
        set(1, uri);
        set(2, directed);
    }
}
