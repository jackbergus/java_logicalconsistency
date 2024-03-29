/*
 * This file is generated by jOOQ.
 */
package org.ufl.aida.ldc.jOOQ.model.tables.records;


import org.ufl.aida.ldc.jOOQ.model.tables.Mentionslot;
import org.jooq.Field;
import org.jooq.Record16;
import org.jooq.Row16;
import org.jooq.impl.TableRecordImpl;

import javax.annotation.Generated;


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
public class MentionslotRecord extends TableRecordImpl<MentionslotRecord> implements Record16<String, String, String, String, String, String[], Object, Object, Object, Object, String, String, String, String, String, String> {

    private static final long serialVersionUID = 332471601;

    /**
     * Setter for <code>public.mentionslot.tree_id</code>.
     */
    public void setTreeId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.mentionslot.tree_id</code>.
     */
    public String getTreeId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>public.mentionslot.id</code>.
     */
    public void setId(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.mentionslot.id</code>.
     */
    public String getId() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.mentionslot.mentionId</code>.
     */
    public void setMentionid(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.mentionslot.mentionId</code>.
     */
    public String getMentionid() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.mentionslot.type</code>.
     */
    public void setType(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.mentionslot.type</code>.
     */
    public String getType() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.mentionslot.subtype</code>.
     */
    public void setSubtype(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.mentionslot.subtype</code>.
     */
    public String getSubtype() {
        return (String) get(4);
    }

    /**
     * Setter for <code>public.mentionslot.attributes</code>.
     */
    public void setAttributes(String... value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.mentionslot.attributes</code>.
     */
    public String[] getAttributes() {
        return (String[]) get(5);
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    public void setTxtEntrypoint(Object value) {
        set(6, value);
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    public Object getTxtEntrypoint() {
        return get(6);
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    public void setSlots(Object value) {
        set(7, value);
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    public Object getSlots() {
        return get(7);
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    public void setStartDate(Object value) {
        set(8, value);
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    public Object getStartDate() {
        return get(8);
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    public void setEndDate(Object value) {
        set(9, value);
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    public Object getEndDate() {
        return get(9);
    }

    /**
     * Setter for <code>public.mentionslot.text_string</code>.
     */
    public void setTextString(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>public.mentionslot.text_string</code>.
     */
    public String getTextString() {
        return (String) get(10);
    }

    /**
     * Setter for <code>public.mentionslot.justification</code>.
     */
    public void setJustification(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>public.mentionslot.justification</code>.
     */
    public String getJustification() {
        return (String) get(11);
    }

    /**
     * Setter for <code>public.mentionslot.topic_id</code>.
     */
    public void setTopicId(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>public.mentionslot.topic_id</code>.
     */
    public String getTopicId() {
        return (String) get(12);
    }

    /**
     * Setter for <code>public.mentionslot.category</code>.
     */
    public void setCategory(String value) {
        set(13, value);
    }

    /**
     * Getter for <code>public.mentionslot.category</code>.
     */
    public String getCategory() {
        return (String) get(13);
    }

    /**
     * Setter for <code>public.mentionslot.handle</code>.
     */
    public void setHandle(String value) {
        set(14, value);
    }

    /**
     * Getter for <code>public.mentionslot.handle</code>.
     */
    public String getHandle() {
        return (String) get(14);
    }

    /**
     * Setter for <code>public.mentionslot.description</code>.
     */
    public void setDescription(String value) {
        set(15, value);
    }

    /**
     * Getter for <code>public.mentionslot.description</code>.
     */
    public String getDescription() {
        return (String) get(15);
    }

    // -------------------------------------------------------------------------
    // Record16 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row16<String, String, String, String, String, String[], Object, Object, Object, Object, String, String, String, String, String, String> fieldsRow() {
        return (Row16) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row16<String, String, String, String, String, String[], Object, Object, Object, Object, String, String, String, String, String, String> valuesRow() {
        return (Row16) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return Mentionslot.MENTIONSLOT.TREE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return Mentionslot.MENTIONSLOT.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return Mentionslot.MENTIONSLOT.MENTIONID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return Mentionslot.MENTIONSLOT.TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return Mentionslot.MENTIONSLOT.SUBTYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String[]> field6() {
        return Mentionslot.MENTIONSLOT.ATTRIBUTES;
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    @Override
    public Field<Object> field7() {
        return Mentionslot.MENTIONSLOT.TXT_ENTRYPOINT;
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    @Override
    public Field<Object> field8() {
        return Mentionslot.MENTIONSLOT.SLOTS;
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    @Override
    public Field<Object> field9() {
        return Mentionslot.MENTIONSLOT.START_DATE;
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    @Override
    public Field<Object> field10() {
        return Mentionslot.MENTIONSLOT.END_DATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field11() {
        return Mentionslot.MENTIONSLOT.TEXT_STRING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field12() {
        return Mentionslot.MENTIONSLOT.JUSTIFICATION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field13() {
        return Mentionslot.MENTIONSLOT.TOPIC_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field14() {
        return Mentionslot.MENTIONSLOT.CATEGORY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field15() {
        return Mentionslot.MENTIONSLOT.HANDLE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field16() {
        return Mentionslot.MENTIONSLOT.DESCRIPTION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component1() {
        return getTreeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getMentionid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component4() {
        return getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component5() {
        return getSubtype();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] component6() {
        return getAttributes();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    @Override
    public Object component7() {
        return getTxtEntrypoint();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    @Override
    public Object component8() {
        return getSlots();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    @Override
    public Object component9() {
        return getStartDate();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    @Override
    public Object component10() {
        return getEndDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component11() {
        return getTextString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component12() {
        return getJustification();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component13() {
        return getTopicId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component14() {
        return getCategory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component15() {
        return getHandle();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component16() {
        return getDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getTreeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getMentionid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getSubtype();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] value6() {
        return getAttributes();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    @Override
    public Object value7() {
        return getTxtEntrypoint();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    @Override
    public Object value8() {
        return getSlots();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    @Override
    public Object value9() {
        return getStartDate();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    @Override
    public Object value10() {
        return getEndDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value11() {
        return getTextString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value12() {
        return getJustification();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value13() {
        return getTopicId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value14() {
        return getCategory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value15() {
        return getHandle();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value16() {
        return getDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MentionslotRecord value1(String value) {
        setTreeId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MentionslotRecord value2(String value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MentionslotRecord value3(String value) {
        setMentionid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MentionslotRecord value4(String value) {
        setType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MentionslotRecord value5(String value) {
        setSubtype(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MentionslotRecord value6(String... value) {
        setAttributes(value);
        return this;
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    @Override
    public MentionslotRecord value7(Object value) {
        setTxtEntrypoint(value);
        return this;
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    @Override
    public MentionslotRecord value8(Object value) {
        setSlots(value);
        return this;
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    @Override
    public MentionslotRecord value9(Object value) {
        setStartDate(value);
        return this;
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using <deprecationOnUnknownTypes/> in your code generator configuration.
     */
    @Deprecated
    @Override
    public MentionslotRecord value10(Object value) {
        setEndDate(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MentionslotRecord value11(String value) {
        setTextString(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MentionslotRecord value12(String value) {
        setJustification(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MentionslotRecord value13(String value) {
        setTopicId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MentionslotRecord value14(String value) {
        setCategory(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MentionslotRecord value15(String value) {
        setHandle(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MentionslotRecord value16(String value) {
        setDescription(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MentionslotRecord values(String value1, String value2, String value3, String value4, String value5, String[] value6, Object value7, Object value8, Object value9, Object value10, String value11, String value12, String value13, String value14, String value15, String value16) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        value13(value13);
        value14(value14);
        value15(value15);
        value16(value16);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached MentionslotRecord
     */
    public MentionslotRecord() {
        super(Mentionslot.MENTIONSLOT);
    }

    /**
     * Create a detached, initialised MentionslotRecord
     */
    public MentionslotRecord(String treeId, String id, String mentionid, String type, String subtype, String[] attributes, Object txtEntrypoint, Object slots, Object startDate, Object endDate, String textString, String justification, String topicId, String category, String handle, String description) {
        super(Mentionslot.MENTIONSLOT);

        set(0, treeId);
        set(1, id);
        set(2, mentionid);
        set(3, type);
        set(4, subtype);
        set(5, attributes);
        set(6, txtEntrypoint);
        set(7, slots);
        set(8, startDate);
        set(9, endDate);
        set(10, textString);
        set(11, justification);
        set(12, topicId);
        set(13, category);
        set(14, handle);
        set(15, description);
    }
}
