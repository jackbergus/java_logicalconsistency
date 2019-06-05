/*
 * This file is generated by jOOQ.
 */
package org.ufl.aida.ta2.tables;


import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.ufl.aida.ta2.Public;
import org.ufl.aida.ta2.tables.records.FactRecord;


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
public class Fact extends TableImpl<FactRecord> {

    private static final long serialVersionUID = 1889509523;

    /**
     * The reference instance of <code>public.fact</code>
     */
    public static final Fact FACT = new Fact();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<FactRecord> getRecordType() {
        return FactRecord.class;
    }

    /**
     * The column <code>public.fact.mid</code>.
     */
    public final TableField<FactRecord, String> MID = createField("mid", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>public.fact.id</code>.
     */
    public final TableField<FactRecord, String> ID = createField("id", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>public.fact.nistTypeLeft</code>.
     */
    public final TableField<FactRecord, String> NISTTYPELEFT = createField("nistTypeLeft", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>public.fact.nistTypeRight</code>.
     */
    public final TableField<FactRecord, String> NISTTYPERIGHT = createField("nistTypeRight", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>public.fact.nistType</code>.
     */
    public final TableField<FactRecord, String> NISTTYPE = createField("nistType", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>public.fact.tree_id</code>.
     */
    public final TableField<FactRecord, String> TREE_ID = createField("tree_id", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>public.fact.partialLabel</code>.
     */
    public final TableField<FactRecord, String> PARTIALLABEL = createField("partialLabel", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>public.fact.nistFullLabel</code>.
     */
    public final TableField<FactRecord, String> NISTFULLLABEL = createField("nistFullLabel", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>public.fact.argumentId</code>.
     */
    public final TableField<FactRecord, String> ARGUMENTID = createField("argumentId", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>public.fact.argumentNistType</code>.
     */
    public final TableField<FactRecord, String> ARGUMENTNISTTYPE = createField("argumentNistType", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>public.fact.argumentRawString</code>.
     */
    public final TableField<FactRecord, String> ARGUMENTRAWSTRING = createField("argumentRawString", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>public.fact.argumentClusterId</code>.
     */
    public final TableField<FactRecord, String> ARGUMENTCLUSTERID = createField("argumentClusterId", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>public.fact.argumentBadlyTranslatedString</code>.
     */
    public final TableField<FactRecord, String> ARGUMENTBADLYTRANSLATEDSTRING = createField("argumentBadlyTranslatedString", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>public.fact.rKind</code>.
     */
    public final TableField<FactRecord, String> RKIND = createField("rKind", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>public.fact.rNistName</code>.
     */
    public final TableField<FactRecord, String> RNISTNAME = createField("rNistName", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>public.fact.resolvedName</code>.
     */
    public final TableField<FactRecord, String> RESOLVEDNAME = createField("resolvedName", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>public.fact.resolvedType</code>.
     */
    public final TableField<FactRecord, String> RESOLVEDTYPE = createField("resolvedType", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>public.fact.score</code>.
     */
    public final TableField<FactRecord, Double> SCORE = createField("score", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>public.fact.scoreEvent</code>.
     */
    public final TableField<FactRecord, Double> SCOREEVENT = createField("scoreEvent", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>public.fact.isNegated</code>.
     */
    public final TableField<FactRecord, Boolean> ISNEGATED = createField("isNegated", org.jooq.impl.SQLDataType.BOOLEAN, this, "");

    /**
     * The column <code>public.fact.isHedged</code>.
     */
    public final TableField<FactRecord, Boolean> ISHEDGED = createField("isHedged", org.jooq.impl.SQLDataType.BOOLEAN, this, "");

    /**
     * The column <code>public.fact.isEventNegated</code>.
     */
    public final TableField<FactRecord, Boolean> ISEVENTNEGATED = createField("isEventNegated", org.jooq.impl.SQLDataType.BOOLEAN, this, "");

    /**
     * The column <code>public.fact.isEventHedged</code>.
     */
    public final TableField<FactRecord, Boolean> ISEVENTHEDGED = createField("isEventHedged", org.jooq.impl.SQLDataType.BOOLEAN, this, "");

    /**
     * The column <code>public.fact.fromFuzzyMatching</code>.
     */
    public final TableField<FactRecord, Boolean> FROMFUZZYMATCHING = createField("fromFuzzyMatching", org.jooq.impl.SQLDataType.BOOLEAN, this, "");

    /**
     * Create a <code>public.fact</code> table reference
     */
    public Fact() {
        this(DSL.name("fact"), null);
    }

    /**
     * Create an aliased <code>public.fact</code> table reference
     */
    public Fact(String alias) {
        this(DSL.name(alias), FACT);
    }

    /**
     * Create an aliased <code>public.fact</code> table reference
     */
    public Fact(Name alias) {
        this(alias, FACT);
    }

    private Fact(Name alias, Table<FactRecord> aliased) {
        this(alias, aliased, null);
    }

    private Fact(Name alias, Table<FactRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> Fact(Table<O> child, ForeignKey<O, FactRecord> key) {
        super(child, key, FACT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Fact as(String alias) {
        return new Fact(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Fact as(Name alias) {
        return new Fact(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Fact rename(String name) {
        return new Fact(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Fact rename(Name name) {
        return new Fact(name, null);
    }
}