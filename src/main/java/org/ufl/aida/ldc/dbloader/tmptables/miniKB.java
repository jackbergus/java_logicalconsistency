package org.ufl.aida.ldc.dbloader.tmptables;

import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.model.SQLType;
import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.model.Table;

import java.io.File;

/**
 * Mapping the miniKB file for each topic into a Java Class.
 *
 * Each topic has a "mini-KB" which includes KEs that were expected to be salient
 * (based on information discovered during topic development and data scouting).
 * The KBs for the topics may have overlapping content; no attempt was made to
 * resolve "coreference" across the KBs.
 */
@Table(sqlTable = "miniKB")
public class miniKB extends SourceTabLoader {

    /**
     * unique identifier for each entry in the KB
     */
    @SQLType(type = "varchar")
    String kb_id;

    @SQLType(type = "varchar")
    String topic_id;

    /**
     * base category of Entity, Relation, Event, or Filler
     *
     * Within the Python Source Code, categories are renamed as Kinds (the best name should be "sorts")
     */
    @SQLType(type = "varchar")
    String category;

    /**
     * name or brief phrase to identify the entry
     */
    @SQLType(type = "varchar")
    String handle;

    @SQLType(type = "varchar")
    String description;

    /**
     * Loads the required information from a string array that was loaded by
     * @param args
     */
    @Override
    public void load(String args[]) {
        this.kb_id = args[0];
        this.topic_id = args[1];
        this.category = args[2];
        this.handle = args[3];
        this.description = args[4];
    }

    @Override
    public File getFile(File parentFolder) {
        return new File(parentFolder, parentFolder.getName()+"_mini-KB.tab");
    }

    @Override
    public miniKB generateNew() {
        return new miniKB();
    }
}
