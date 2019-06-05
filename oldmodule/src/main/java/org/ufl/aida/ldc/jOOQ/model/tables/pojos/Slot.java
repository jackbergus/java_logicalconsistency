/*
 * This file is generated by jOOQ.
 */
package org.ufl.aida.ldc.jOOQ.model.tables.pojos;


import javax.annotation.Generated;
import java.io.Serializable;


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
public class Slot implements Serializable {

    private static final long serialVersionUID = 1401226047;

    private String treeId;
    private String mentionid;
    private String slotType;
    private String slotAttribute;
    private String argId;

    public Slot() {}

    public Slot(Slot value) {
        this.treeId = value.treeId;
        this.mentionid = value.mentionid;
        this.slotType = value.slotType;
        this.slotAttribute = value.slotAttribute;
        this.argId = value.argId;
    }

    public Slot(
        String treeId,
        String mentionid,
        String slotType,
        String slotAttribute,
        String argId
    ) {
        this.treeId = treeId;
        this.mentionid = mentionid;
        this.slotType = slotType;
        this.slotAttribute = slotAttribute;
        this.argId = argId;
    }

    public String getTreeId() {
        return this.treeId;
    }

    public void setTreeId(String treeId) {
        this.treeId = treeId;
    }

    public String getMentionid() {
        return this.mentionid;
    }

    public void setMentionid(String mentionid) {
        this.mentionid = mentionid;
    }

    public String getSlotType() {
        return this.slotType;
    }

    public void setSlotType(String slotType) {
        this.slotType = slotType;
    }

    public String getSlotAttribute() {
        return this.slotAttribute;
    }

    public void setSlotAttribute(String slotAttribute) {
        this.slotAttribute = slotAttribute;
    }

    public String getArgId() {
        return this.argId;
    }

    public void setArgId(String argId) {
        this.argId = argId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Slot (");

        sb.append(treeId);
        sb.append(", ").append(mentionid);
        sb.append(", ").append(slotType);
        sb.append(", ").append(slotAttribute);
        sb.append(", ").append(argId);

        sb.append(")");
        return sb.toString();
    }
}