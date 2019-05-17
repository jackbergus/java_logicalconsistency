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
public class EvtSlots extends SourceTabLoader {

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
     * For event slots only, each argument can have an attribute of "hedged" and/or
     * "not".  The meaning of the attributes is the same as described above in the
     * Mentions section, but in this case its scope is at the slot level.  So if a
     * document asserts that "it wasn't a BUK missile that shot down MH17" the
     * Conflict.Attack mention itself would not have "hedged"/"not" attributes
     * assigned to it, but the BUK missile filler would have a "not" attribute shown
     * in the slots table.
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
        this.slot_attribute = args[3];
        this.arg_id = args[4];
    }

    @Override
    public File getFile(File parentFolder) {
        return new File(parentFolder, parentFolder.getName()+"_evt_slots.tab");
    }

    @Override
    public EvtSlots generateNew() {
        return new EvtSlots();
    }
}
