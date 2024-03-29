/*
 * This file is generated by jOOQ.
 */
package org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.tables.records;


import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.tables.Edges;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record7;
import org.jooq.Row7;
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
public class EdgesRecord extends UpdatableRecordImpl<EdgesRecord> implements Record7<Integer, String, Integer, Integer, Integer, Float, Object> {

    private static final long serialVersionUID = -590248357;

    /**
     * Setter for <code>public.edges.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.edges.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>public.edges.uri</code>.
     */
    public void setUri(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.edges.uri</code>.
     */
    public String getUri() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.edges.relation_id</code>.
     */
    public void setRelationId(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.edges.relation_id</code>.
     */
    public Integer getRelationId() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>public.edges.start_id</code>.
     */
    public void setStartId(Integer value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.edges.start_id</code>.
     */
    public Integer getStartId() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>public.edges.end_id</code>.
     */
    public void setEndId(Integer value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.edges.end_id</code>.
     */
    public Integer getEndId() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>public.edges.weight</code>.
     */
    public void setWeight(Float value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.edges.weight</code>.
     */
    public Float getWeight() {
        return (Float) get(5);
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    public void setData(Object value) {
        set(6, value);
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    public Object getData() {
        return get(6);
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
    // Record7 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row7<Integer, String, Integer, Integer, Integer, Float, Object> fieldsRow() {
        return (Row7) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row7<Integer, String, Integer, Integer, Integer, Float, Object> valuesRow() {
        return (Row7) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return Edges.EDGES.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return Edges.EDGES.URI;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field3() {
        return Edges.EDGES.RELATION_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field4() {
        return Edges.EDGES.START_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field5() {
        return Edges.EDGES.END_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Float> field6() {
        return Edges.EDGES.WEIGHT;
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    @Override
    public Field<Object> field7() {
        return Edges.EDGES.DATA;
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
    public Integer component3() {
        return getRelationId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component4() {
        return getStartId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component5() {
        return getEndId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Float component6() {
        return getWeight();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    @Override
    public Object component7() {
        return getData();
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
    public Integer value3() {
        return getRelationId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value4() {
        return getStartId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value5() {
        return getEndId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Float value6() {
        return getWeight();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    @Override
    public Object value7() {
        return getData();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EdgesRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EdgesRecord value2(String value) {
        setUri(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EdgesRecord value3(Integer value) {
        setRelationId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EdgesRecord value4(Integer value) {
        setStartId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EdgesRecord value5(Integer value) {
        setEndId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EdgesRecord value6(Float value) {
        setWeight(value);
        return this;
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    @Override
    public EdgesRecord value7(Object value) {
        setData(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EdgesRecord values(Integer value1, String value2, Integer value3, Integer value4, Integer value5, Float value6, Object value7) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached EdgesRecord
     */
    public EdgesRecord() {
        super(Edges.EDGES);
    }

    /**
     * Create a detached, initialised EdgesRecord
     */
    public EdgesRecord(Integer id, String uri, Integer relationId, Integer startId, Integer endId, Float weight, Object data) {
        super(Edges.EDGES);

        set(0, id);
        set(1, uri);
        set(2, relationId);
        set(3, startId);
        set(4, endId);
        set(5, weight);
        set(6, data);
    }
}
