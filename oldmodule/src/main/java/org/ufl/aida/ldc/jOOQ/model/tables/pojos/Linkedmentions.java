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
public class Linkedmentions implements Serializable {

    private static final long serialVersionUID = 959522121;

    private String treeId;
    private String mentionid;
    private String id;
    private String provenance;
    private String handle;
    private String textString;
    private String justification;
    private String description;
    private String category;
    private String type;
    private String subtype;
    private String attribute;
    private String attribute2;
    private String startDateType;
    private String startDate;
    private String endDateType;
    private String endDate;
    private String kbId;
    private String topicId;
    private String textoffsetStartchar;
    private String textoffsetEndchar;

    public Linkedmentions() {}

    public Linkedmentions(Linkedmentions value) {
        this.treeId = value.treeId;
        this.mentionid = value.mentionid;
        this.id = value.id;
        this.provenance = value.provenance;
        this.handle = value.handle;
        this.textString = value.textString;
        this.justification = value.justification;
        this.description = value.description;
        this.category = value.category;
        this.type = value.type;
        this.subtype = value.subtype;
        this.attribute = value.attribute;
        this.attribute2 = value.attribute2;
        this.startDateType = value.startDateType;
        this.startDate = value.startDate;
        this.endDateType = value.endDateType;
        this.endDate = value.endDate;
        this.kbId = value.kbId;
        this.topicId = value.topicId;
        this.textoffsetStartchar = value.textoffsetStartchar;
        this.textoffsetEndchar = value.textoffsetEndchar;
    }

    public Linkedmentions(
        String treeId,
        String mentionid,
        String id,
        String provenance,
        String handle,
        String textString,
        String justification,
        String description,
        String category,
        String type,
        String subtype,
        String attribute,
        String attribute2,
        String startDateType,
        String startDate,
        String endDateType,
        String endDate,
        String kbId,
        String topicId,
        String textoffsetStartchar,
        String textoffsetEndchar
    ) {
        this.treeId = treeId;
        this.mentionid = mentionid;
        this.id = id;
        this.provenance = provenance;
        this.handle = handle;
        this.textString = textString;
        this.justification = justification;
        this.description = description;
        this.category = category;
        this.type = type;
        this.subtype = subtype;
        this.attribute = attribute;
        this.attribute2 = attribute2;
        this.startDateType = startDateType;
        this.startDate = startDate;
        this.endDateType = endDateType;
        this.endDate = endDate;
        this.kbId = kbId;
        this.topicId = topicId;
        this.textoffsetStartchar = textoffsetStartchar;
        this.textoffsetEndchar = textoffsetEndchar;
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

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProvenance() {
        return this.provenance;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    public String getHandle() {
        return this.handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getTextString() {
        return this.textString;
    }

    public void setTextString(String textString) {
        this.textString = textString;
    }

    public String getJustification() {
        return this.justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubtype() {
        return this.subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getAttribute() {
        return this.attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getAttribute2() {
        return this.attribute2;
    }

    public void setAttribute2(String attribute2) {
        this.attribute2 = attribute2;
    }

    public String getStartDateType() {
        return this.startDateType;
    }

    public void setStartDateType(String startDateType) {
        this.startDateType = startDateType;
    }

    public String getStartDate() {
        return this.startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDateType() {
        return this.endDateType;
    }

    public void setEndDateType(String endDateType) {
        this.endDateType = endDateType;
    }

    public String getEndDate() {
        return this.endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getKbId() {
        return this.kbId;
    }

    public void setKbId(String kbId) {
        this.kbId = kbId;
    }

    public String getTopicId() {
        return this.topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getTextoffsetStartchar() {
        return this.textoffsetStartchar;
    }

    public void setTextoffsetStartchar(String textoffsetStartchar) {
        this.textoffsetStartchar = textoffsetStartchar;
    }

    public String getTextoffsetEndchar() {
        return this.textoffsetEndchar;
    }

    public void setTextoffsetEndchar(String textoffsetEndchar) {
        this.textoffsetEndchar = textoffsetEndchar;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Linkedmentions (");

        sb.append(treeId);
        sb.append(", ").append(mentionid);
        sb.append(", ").append(id);
        sb.append(", ").append(provenance);
        sb.append(", ").append(handle);
        sb.append(", ").append(textString);
        sb.append(", ").append(justification);
        sb.append(", ").append(description);
        sb.append(", ").append(category);
        sb.append(", ").append(type);
        sb.append(", ").append(subtype);
        sb.append(", ").append(attribute);
        sb.append(", ").append(attribute2);
        sb.append(", ").append(startDateType);
        sb.append(", ").append(startDate);
        sb.append(", ").append(endDateType);
        sb.append(", ").append(endDate);
        sb.append(", ").append(kbId);
        sb.append(", ").append(topicId);
        sb.append(", ").append(textoffsetStartchar);
        sb.append(", ").append(textoffsetEndchar);

        sb.append(")");
        return sb.toString();
    }
}