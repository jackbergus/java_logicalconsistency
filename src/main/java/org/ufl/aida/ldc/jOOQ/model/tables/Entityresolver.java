/*
 * This file is generated by jOOQ.
 */
package org.ufl.aida.ldc.jOOQ.model.tables;


import org.ufl.aida.ldc.jOOQ.model.Indexes;
import org.ufl.aida.ldc.jOOQ.model.Public;
import org.ufl.aida.ldc.jOOQ.model.tables.records.EntityresolverRecord;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;

import javax.annotation.Generated;
import java.util.Arrays;
import java.util.List;


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
public class Entityresolver extends TableImpl<EntityresolverRecord> {

    private static final long serialVersionUID = -1991385745;

    /**
     * The reference instance of <code>public.entityResolver</code>
     */
    public static final Entityresolver ENTITYRESOLVER = new Entityresolver();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<EntityresolverRecord> getRecordType() {
        return EntityresolverRecord.class;
    }

    /**
     * The column <code>public.entityResolver.handle</code>.
     */
    public final TableField<EntityresolverRecord, String> HANDLE = createField("handle", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>public.entityResolver.category</code>.
     */
    public final TableField<EntityresolverRecord, String> CATEGORY = createField("category", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>public.entityResolver.type</code>.
     */
    public final TableField<EntityresolverRecord, String> TYPE = createField("type", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>public.entityResolver.subtype</code>.
     */
    public final TableField<EntityresolverRecord, String> SUBTYPE = createField("subtype", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>public.entityResolver.attributes</code>.
     */
    public final TableField<EntityresolverRecord, String[]> ATTRIBUTES = createField("attributes", org.jooq.impl.SQLDataType.VARCHAR.getArrayDataType(), this, "");

    /**
     * The column <code>public.entityResolver.attributes2</code>.
     */
    public final TableField<EntityresolverRecord, String[]> ATTRIBUTES2 = createField("attributes2", org.jooq.impl.SQLDataType.VARCHAR.getArrayDataType(), this, "");

    /**
     * The column <code>public.entityResolver.kbids</code>.
     */
    public final TableField<EntityresolverRecord, String[]> KBIDS = createField("kbids", org.jooq.impl.SQLDataType.VARCHAR.getArrayDataType(), this, "");

    /**
     * The column <code>public.entityResolver.descriptions</code>.
     */
    public final TableField<EntityresolverRecord, String[]> DESCRIPTIONS = createField("descriptions", org.jooq.impl.SQLDataType.VARCHAR.getArrayDataType(), this, "");

    /**
     * The column <code>public.entityResolver.justifications</code>.
     */
    public final TableField<EntityresolverRecord, String[]> JUSTIFICATIONS = createField("justifications", org.jooq.impl.SQLDataType.VARCHAR.getArrayDataType(), this, "");

    /**
     * The column <code>public.entityResolver.text_strings</code>.
     */
    public final TableField<EntityresolverRecord, String[]> TEXT_STRINGS = createField("text_strings", org.jooq.impl.SQLDataType.VARCHAR.getArrayDataType(), this, "");

    /**
     * The column <code>public.entityResolver.ids</code>.
     */
    public final TableField<EntityresolverRecord, String[]> IDS = createField("ids", org.jooq.impl.SQLDataType.VARCHAR.getArrayDataType(), this, "");

    /**
     * The column <code>public.entityResolver.mids</code>.
     */
    public final TableField<EntityresolverRecord, String[]> MIDS = createField("mids", org.jooq.impl.SQLDataType.VARCHAR.getArrayDataType(), this, "");

    /**
     * Create a <code>public.entityResolver</code> table reference
     */
    public Entityresolver() {
        this(DSL.name("entityResolver"), null);
    }

    /**
     * Create an aliased <code>public.entityResolver</code> table reference
     */
    public Entityresolver(String alias) {
        this(DSL.name(alias), ENTITYRESOLVER);
    }

    /**
     * Create an aliased <code>public.entityResolver</code> table reference
     */
    public Entityresolver(Name alias) {
        this(alias, ENTITYRESOLVER);
    }

    private Entityresolver(Name alias, Table<EntityresolverRecord> aliased) {
        this(alias, aliased, null);
    }

    private Entityresolver(Name alias, Table<EntityresolverRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> Entityresolver(Table<O> child, ForeignKey<O, EntityresolverRecord> key) {
        super(child, key, ENTITYRESOLVER);
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
        return Arrays.<Index>asList(Indexes.HANDLESEARCH);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Entityresolver as(String alias) {
        return new Entityresolver(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Entityresolver as(Name alias) {
        return new Entityresolver(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Entityresolver rename(String name) {
        return new Entityresolver(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Entityresolver rename(Name name) {
        return new Entityresolver(name, null);
    }
}
