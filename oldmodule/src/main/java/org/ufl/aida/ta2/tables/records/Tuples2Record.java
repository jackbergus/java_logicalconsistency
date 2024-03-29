/*
 * This file is generated by jOOQ.
 */
package org.ufl.aida.ta2.tables.records;


import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record7;
import org.jooq.Row7;
import org.jooq.impl.TableRecordImpl;
import org.ufl.aida.ta2.tables.Tuples2;
import org.ufl.aida.ta2.tables.interfaces.ITuples2;


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
public class Tuples2Record extends TableRecordImpl<Tuples2Record> implements Record7<String, String, Double, Boolean, Boolean, String[], Object>, ITuples2 {

    private static final long serialVersionUID = -2027919201;

    /**
     * Setter for <code>public.tuples2.mid</code>.
     */
    @Override
    public void setMid(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.tuples2.mid</code>.
     */
    @Override
    public String getMid() {
        return (String) get(0);
    }

    /**
     * Setter for <code>public.tuples2.nistType</code>.
     */
    @Override
    public void setNisttype(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.tuples2.nistType</code>.
     */
    @Override
    public String getNisttype() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.tuples2.scoreEvent</code>.
     */
    @Override
    public void setScoreevent(Double value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.tuples2.scoreEvent</code>.
     */
    @Override
    public Double getScoreevent() {
        return (Double) get(2);
    }

    /**
     * Setter for <code>public.tuples2.negated</code>.
     */
    @Override
    public void setNegated(Boolean value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.tuples2.negated</code>.
     */
    @Override
    public Boolean getNegated() {
        return (Boolean) get(3);
    }

    /**
     * Setter for <code>public.tuples2.hedged</code>.
     */
    @Override
    public void setHedged(Boolean value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.tuples2.hedged</code>.
     */
    @Override
    public Boolean getHedged() {
        return (Boolean) get(4);
    }

    /**
     * Setter for <code>public.tuples2.constituent</code>.
     */
    @Override
    public void setConstituent(String... value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.tuples2.constituent</code>.
     */
    @Override
    public String[] getConstituent() {
        return (String[]) get(5);
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    @Override
    public void setJsonObjectAgg(Object value) {
        set(6, value);
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    @Override
    public Object getJsonObjectAgg() {
        return get(6);
    }

    // -------------------------------------------------------------------------
    // Record7 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row7<String, String, Double, Boolean, Boolean, String[], Object> fieldsRow() {
        return (Row7) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row7<String, String, Double, Boolean, Boolean, String[], Object> valuesRow() {
        return (Row7) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return Tuples2.TUPLES2.MID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return Tuples2.TUPLES2.NISTTYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Double> field3() {
        return Tuples2.TUPLES2.SCOREEVENT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field4() {
        return Tuples2.TUPLES2.NEGATED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field5() {
        return Tuples2.TUPLES2.HEDGED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String[]> field6() {
        return Tuples2.TUPLES2.CONSTITUENT;
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    @Override
    public Field<Object> field7() {
        return Tuples2.TUPLES2.JSON_OBJECT_AGG;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component1() {
        return getMid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getNisttype();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double component3() {
        return getScoreevent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean component4() {
        return getNegated();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean component5() {
        return getHedged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] component6() {
        return getConstituent();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    @Override
    public Object component7() {
        return getJsonObjectAgg();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getMid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getNisttype();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double value3() {
        return getScoreevent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value4() {
        return getNegated();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value5() {
        return getHedged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] value6() {
        return getConstituent();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    @Override
    public Object value7() {
        return getJsonObjectAgg();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tuples2Record value1(String value) {
        setMid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tuples2Record value2(String value) {
        setNisttype(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tuples2Record value3(Double value) {
        setScoreevent(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tuples2Record value4(Boolean value) {
        setNegated(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tuples2Record value5(Boolean value) {
        setHedged(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tuples2Record value6(String... value) {
        setConstituent(value);
        return this;
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    @Override
    public Tuples2Record value7(Object value) {
        setJsonObjectAgg(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tuples2Record values(String value1, String value2, Double value3, Boolean value4, Boolean value5, String[] value6, Object value7) {
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
    // FROM and INTO
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public void from(ITuples2 from) {
        setMid(from.getMid());
        setNisttype(from.getNisttype());
        setScoreevent(from.getScoreevent());
        setNegated(from.getNegated());
        setHedged(from.getHedged());
        setConstituent(from.getConstituent());
        setJsonObjectAgg(from.getJsonObjectAgg());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E extends ITuples2> E into(E into) {
        into.from(this);
        return into;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached Tuples2Record
     */
    public Tuples2Record() {
        super(Tuples2.TUPLES2);
    }

    /**
     * Create a detached, initialised Tuples2Record
     */
    public Tuples2Record(String mid, String nisttype, Double scoreevent, Boolean negated, Boolean hedged, String[] constituent, Object jsonObjectAgg) {
        super(Tuples2.TUPLES2);

        set(0, mid);
        set(1, nisttype);
        set(2, scoreevent);
        set(3, negated);
        set(4, hedged);
        set(5, constituent);
        set(6, jsonObjectAgg);
    }
}
