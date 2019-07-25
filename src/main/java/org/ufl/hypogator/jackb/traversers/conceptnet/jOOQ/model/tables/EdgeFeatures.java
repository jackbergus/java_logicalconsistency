/*
 * This file is generated by jOOQ.
 */
package org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.tables;


import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.Indexes;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.Keys;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.Public;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.model.tables.records.EdgeFeaturesRecord;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


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
public class EdgeFeatures extends TableImpl<EdgeFeaturesRecord> {

    private static final long serialVersionUID = 1393756558;

    /**
     * The reference instance of <code>public.edge_features</code>
     */
    public static final EdgeFeatures EDGE_FEATURES = new EdgeFeatures();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<EdgeFeaturesRecord> getRecordType() {
        return EdgeFeaturesRecord.class;
    }

    /**
     * The column <code>public.edge_features.rel_id</code>.
     */
    public final TableField<EdgeFeaturesRecord, Integer> REL_ID = createField("rel_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>public.edge_features.direction</code>.
     */
    public final TableField<EdgeFeaturesRecord, Integer> DIRECTION = createField("direction", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>public.edge_features.node_id</code>.
     */
    public final TableField<EdgeFeaturesRecord, Integer> NODE_ID = createField("node_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>public.edge_features.edge_id</code>.
     */
    public final TableField<EdgeFeaturesRecord, Integer> EDGE_ID = createField("edge_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * Create a <code>public.edge_features</code> table reference
     */
    public EdgeFeatures() {
        this(DSL.name("edge_features"), null);
    }

    /**
     * Create an aliased <code>public.edge_features</code> table reference
     */
    public EdgeFeatures(String alias) {
        this(DSL.name(alias), EDGE_FEATURES);
    }

    /**
     * Create an aliased <code>public.edge_features</code> table reference
     */
    public EdgeFeatures(Name alias) {
        this(alias, EDGE_FEATURES);
    }

    private EdgeFeatures(Name alias, Table<EdgeFeaturesRecord> aliased) {
        this(alias, aliased, null);
    }

    private EdgeFeatures(Name alias, Table<EdgeFeaturesRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> EdgeFeatures(Table<O> child, ForeignKey<O, EdgeFeaturesRecord> key) {
        super(child, key, EDGE_FEATURES);
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
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.EF_FEATURE, Indexes.EF_NODE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<EdgeFeaturesRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<EdgeFeaturesRecord, ?>>asList(Keys.EDGE_FEATURES__EDGE_FEATURES_REL_ID_FKEY, Keys.EDGE_FEATURES__EDGE_FEATURES_NODE_ID_FKEY, Keys.EDGE_FEATURES__EDGE_FEATURES_EDGE_ID_FKEY);
    }

    public Relations relations() {
        return new Relations(this, Keys.EDGE_FEATURES__EDGE_FEATURES_REL_ID_FKEY);
    }

    public Nodes nodes() {
        return new Nodes(this, Keys.EDGE_FEATURES__EDGE_FEATURES_NODE_ID_FKEY);
    }

    public Edges edges() {
        return new Edges(this, Keys.EDGE_FEATURES__EDGE_FEATURES_EDGE_ID_FKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EdgeFeatures as(String alias) {
        return new EdgeFeatures(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EdgeFeatures as(Name alias) {
        return new EdgeFeatures(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public EdgeFeatures rename(String name) {
        return new EdgeFeatures(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public EdgeFeatures rename(Name name) {
        return new EdgeFeatures(name, null);
    }
}