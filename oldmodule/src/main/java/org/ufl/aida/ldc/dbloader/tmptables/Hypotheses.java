package org.ufl.aida.ldc.dbloader.tmptables;

import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.model.SQLType;
import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.model.Table;

import java.io.File;

/**
 * There is a single hypothesis table for each topic.  For each hypothesis, the
 * table provides a judgement for each relation or event KE in the mentions
 * tables as to whether it supports the hypothesis.
 *
 * The mention IDs for each event or relation shown in the hypothesis table must
 * be looked up in the mentions table and the slots table to find the details for
 * the event or relation.  Thus, the full set of KEs that support a hypothesis
 * will include the event and relation mentions that were judged as relevant,
 * plus the entities and fillers included in the slots table for those
 * events/relations.
 */
@Table(sqlTable = "hypothesis")
public class Hypotheses extends SourceTabLoader {
    @SQLType(type = "varchar")
    String tree_id;

    @SQLType(type = "varchar")
    String hypothesis_id;

    @SQLType(type = "varchar")
    String mentionId;

    /**
     * During annotation, the annotator views all relations and events associated
     * with each entity in the current document (root document).  For each relation
     * and event mention, the annotator indicates whether the hypothesis is fully
     * supported, partially supported, or contradicted by the relation/event mention.
     * If the relation/event mention is irrelevant to the hypothesis, they choose
     * "not relevant".  Annotators are instructed to use the following criteria to
     * choose a relevance value for each relation/event-hypothesis pair:
     *
     *  "fully-relevant": Given this relation/event mention, this hypothesis must be
     *  true.
     *
     *  "partially-relevant": Given this relation/event mention, this hypothesis could
     *  be true.
     *
     *  Contradicted: Given this relation/event mention, this hypothesis cannot be
     *  true.
     *
     * "n/a": This relation/event mention neither supports nor contradicts
     *  this hypothesis.
     */
    @SQLType(type = "varchar")
    String value;

    @Override
    public void load(String[] args) {
        this.tree_id = args[0];
        this.hypothesis_id = args[1];
        this.mentionId = args[2];
        this.value = args[3];
    }


    @Override
    public File getFile(File parentFolder) {
        return new File(parentFolder, parentFolder.getName()+"_hypotheses.tab");
    }

    @Override
    public Hypotheses generateNew() {
        return new Hypotheses();
    }
}
