/*
 * This file is generated by jOOQ.
 */
package org.ufl.aida.ta2;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;
import org.ufl.aida.ta2.tables.Expansions;
import org.ufl.aida.ta2.tables.Fact;
import org.ufl.aida.ta2.tables.MentionsForUpdate;
import org.ufl.aida.ta2.tables.Tuples;
import org.ufl.aida.ta2.tables.Tuples2;


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
public class Public extends SchemaImpl {

    private static final long serialVersionUID = -394332921;

    /**
     * The reference instance of <code>public</code>
     */
    public static final Public PUBLIC = new Public();

    /**
     * The table <code>public.expansions</code>.
     */
    public final Expansions EXPANSIONS = Expansions.EXPANSIONS;

    /**
     * The table <code>public.fact</code>.
     */
    public final Fact FACT = Fact.FACT;

    /**
     * The table <code>public.mentions_for_update</code>.
     */
    public final MentionsForUpdate MENTIONS_FOR_UPDATE = MentionsForUpdate.MENTIONS_FOR_UPDATE;

    /**
     * The table <code>public.tuples</code>.
     */
    public final Tuples TUPLES = Tuples.TUPLES;

    /**
     * The table <code>public.tuples2</code>.
     */
    public final Tuples2 TUPLES2 = Tuples2.TUPLES2;

    /**
     * No further instances allowed
     */
    private Public() {
        super("public", null);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        List result = new ArrayList();
        result.addAll(getTables0());
        return result;
    }

    private final List<Table<?>> getTables0() {
        return Arrays.<Table<?>>asList(
            Expansions.EXPANSIONS,
            Fact.FACT,
            MentionsForUpdate.MENTIONS_FOR_UPDATE,
            Tuples.TUPLES,
            Tuples2.TUPLES2);
    }
}