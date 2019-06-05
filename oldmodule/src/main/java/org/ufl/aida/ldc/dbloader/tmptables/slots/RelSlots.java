package org.ufl.aida.ldc.dbloader.tmptables.slots;

import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.model.SQLType;
import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.model.Table;
import org.ufl.aida.ldc.dbloader.tmptables.SourceTabLoader;

import java.io.File;

/**
 * There are two slots tables per topic, one for relations and one for events.
 * Relation and event mentions in the mentions tables must be looked up in the
 * slots tables to find the arguments and fillers involved in the relation/event.
 *
 * For each relation or event mention, annotators record which entities or
 * fillers participate in the relation/event.  Relation/event arguments/fillers
 * must be present in the same document element as the relation/event mention in
 * order to be annotated for that mention.
 */
@Table(sqlTable = "slot")
public class RelSlots extends SourceTabLoader {

    @SQLType(type = "varchar")
    public String tree_id;

    @SQLType(type = "varchar")
    public String mentionId;

    /**
     * In addition to the entity/filler
     * id, annotators chose a "slot type" which corresponds to something like a role
     * in the relation or event (e.g. a Conflict.Demonstrate event has possible slot
     * types of Person or Organization, Place, and Date).
     */
    @SQLType(type = "varchar")
    public String slot_type;

    /**
     * Unused field
     */
    @SQLType(type = "varchar")
    public String slot_attribute;

    @SQLType(type = "varchar")
    public String arg_id;

    @Override
    public void load(String args[]) {
        this.tree_id = args[0];
        this.mentionId = args[1];
        this.slot_type = args[2];
        this.arg_id = args[3];
    }

    @Override
    public File getFile(File parentFolder) {
        return new File(parentFolder, parentFolder.getName()+"_rel_slots.tab");
    }

    @Override
    public RelSlots generateNew() {
        return new RelSlots();
    }
}
