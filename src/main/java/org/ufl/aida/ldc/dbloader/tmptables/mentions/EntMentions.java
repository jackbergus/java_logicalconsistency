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
public class EntMentions extends SourceTabLoader {

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
     * Unused
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
    public void load(String fields[]) {
        this.tree_id = fields[0];
        this.mentionId = fields[1];
        this.id = fields[2];
        this.provenance = fields[3];
        this.textoffset_startchar = fields[4];
        this.textoffset_endchar = fields[5];
        this.text_string = fields[6];
        this.justification = fields[7];
        this.type = fields[8];
        this.subtype = fields[9];
        this.kb_id = fields[10];
    }

    @Override
    public File getFile(File parentFolder) {
        return new File(parentFolder, parentFolder.getName()+"_ent_mentions.tab");
    }

    @Override
    public EntMentions generateNew() {
        return new EntMentions();
    }
}
