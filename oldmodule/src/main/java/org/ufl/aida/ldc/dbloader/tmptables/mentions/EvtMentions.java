package org.ufl.aida.ldc.dbloader.tmptables.mentions;

import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.model.SQLType;
import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.model.Table;
import org.ufl.aida.ldc.dbloader.tmptables.SourceTabLoader;

import java.io.File;

/**
 *  These tables contain information about
 * each annotated mention, including a KB-id linking it to the topic-based
 * mini-KB or a NIL-id for mentions which were not present in the mini-KB.
 */
@Table(sqlTable = "mention")
public class EvtMentions extends SourceTabLoader {
    /**
     * Only one mention per document element is annotated.  So if a root document
     * (the original page seen on the internet) has 1 text, 2 image, and 1 video
     * document elements, an entity that was "mentioned" in all of the document
     * elements would have 4 mentions coming from the annotation of this root
     * document, one in each of the document elements.
     */
    @SQLType(type = "varchar")
    public String  tree_id;

    /** The exception to the one
     * mention per document element rule is for relation or event mentions when one
     * or more of the arguments, attributes, or type/subtype differ between two
     * mentions of the same relation/event.  In such cases, one mention is created
     * for each occurrence of the relation/event that differs from the mentions
     * already annotated.  For example, if a document element contains both an
     * assertion that MH17 was shot down by a missile and an assertion that it was
     * shot down by a fighter jet, two separate mentions would be created.
     */
    @SQLType(type = "varchar")
    public String  mentionId;

    @SQLType(type = "varchar")
    public String  id;

    @SQLType(type = "varchar")
    public String  provenance;

    @SQLType(type = "varchar")
    public String  textoffset_startchar;

    @SQLType(type = "varchar")
    public String  textoffset_endchar;

    @SQLType(type = "varchar")
    public String  text_string;

    @SQLType(type = "varchar")
    public String  justification;

    @SQLType(type = "varchar")
    public String  type;

    @SQLType(type = "varchar")
    public String  subtype;

    /**
     * In addition, relation and event mentions can have attributes associated with
     * them.  Relations and events can have the belief-type attributes "hedged"
     * and/or "not" associated with them.  "Hedged" is used to indicate uncertainty
     * (as reported by the source, not the annotator's certainty), and "not" is used
     * to indicate that the source asserts that the event or relation did not happen.
     * A mention can have both "hedged" and "not", which would indicate that that
     * source asserted that the relation or event possibly/likely did not happen.
     */
    @SQLType(type = "varchar")
    public String  attribute;

    /**
     * Events can have an additional attribute of "deliberate" or "accidental".
     * These are used to capture assertions by the source about whether the event was
     * intentional or not.  Annotators use one of these attributes only when the
     * source explicitly conveys an assertion about intentionality, especially where
     * such assertions are crucial to understanding informational conflict.
     */
    @SQLType(type = "varchar")
    public String  attribute2;

    @SQLType(type = "varchar")
    public String  start_date_type;

    @SQLType(type = "varchar")
    public String  start_date;

    @SQLType(type = "varchar")
    public String  end_date_type;

    @SQLType(type = "varchar")
    public String  end_date;

    @SQLType(type = "varchar")
    public String  kb_id;

    @Override
    public void load(String args[]) {
        this.tree_id = args[0];
        this.mentionId = args[1];
        this.id = args[2];
        this.provenance = args[3];
        this.textoffset_startchar = args[4];
        this.textoffset_endchar = args[5];
        this.text_string = args[6];
        this.justification = args[7];
        this.type = args[8];
        this.subtype = args[9];
        this.attribute = args[10];
        this.attribute2 = args[11];
        this.start_date_type = args[12];
        this.start_date = args[13];
        this.end_date_type = args[14];
        this.end_date = args[15];
        this.kb_id = args[16];
    }

    @Override
    public File getFile(File parentFolder) {
        return new File(parentFolder, parentFolder.getName()+"_evt_mentions.tab");
    }

    @Override
    public EvtMentions generateNew() {
        return new EvtMentions();
    }
}
