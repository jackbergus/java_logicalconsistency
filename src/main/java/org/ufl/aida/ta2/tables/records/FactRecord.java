/*
 * This file is generated by jOOQ.
 */
package org.ufl.aida.ta2.tables.records;


import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record19;
import org.jooq.Row19;
import org.jooq.impl.TableRecordImpl;
import org.ufl.aida.ta2.tables.Fact;
import org.ufl.aida.ta2.tables.interfaces.IFact;


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
public class FactRecord extends TableRecordImpl<FactRecord> implements Record19<String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, Double, Boolean>, IFact {

    private static final long serialVersionUID = -60042872;

    /**
     * Setter for <code>public.fact.mid</code>.
     */
    @Override
    public void setMid(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.fact.mid</code>.
     */
    @Override
    public String getMid() {
        return (String) get(0);
    }

    /**
     * Setter for <code>public.fact.id</code>.
     */
    @Override
    public void setId(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.fact.id</code>.
     */
    @Override
    public String getId() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.fact.nistTypeLeft</code>.
     */
    @Override
    public void setNisttypeleft(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.fact.nistTypeLeft</code>.
     */
    @Override
    public String getNisttypeleft() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.fact.nistTypeRight</code>.
     */
    @Override
    public void setNisttyperight(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.fact.nistTypeRight</code>.
     */
    @Override
    public String getNisttyperight() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.fact.nistType</code>.
     */
    @Override
    public void setNisttype(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.fact.nistType</code>.
     */
    @Override
    public String getNisttype() {
        return (String) get(4);
    }

    /**
     * Setter for <code>public.fact.tree_id</code>.
     */
    @Override
    public void setTreeId(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.fact.tree_id</code>.
     */
    @Override
    public String getTreeId() {
        return (String) get(5);
    }

    /**
     * Setter for <code>public.fact.partialLabel</code>.
     */
    @Override
    public void setPartiallabel(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.fact.partialLabel</code>.
     */
    @Override
    public String getPartiallabel() {
        return (String) get(6);
    }

    /**
     * Setter for <code>public.fact.nistFullLabel</code>.
     */
    @Override
    public void setNistfulllabel(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.fact.nistFullLabel</code>.
     */
    @Override
    public String getNistfulllabel() {
        return (String) get(7);
    }

    /**
     * Setter for <code>public.fact.argumentId</code>.
     */
    @Override
    public void setArgumentid(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.fact.argumentId</code>.
     */
    @Override
    public String getArgumentid() {
        return (String) get(8);
    }

    /**
     * Setter for <code>public.fact.argumentNistType</code>.
     */
    @Override
    public void setArgumentnisttype(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>public.fact.argumentNistType</code>.
     */
    @Override
    public String getArgumentnisttype() {
        return (String) get(9);
    }

    /**
     * Setter for <code>public.fact.argumentRawString</code>.
     */
    @Override
    public void setArgumentrawstring(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>public.fact.argumentRawString</code>.
     */
    @Override
    public String getArgumentrawstring() {
        return (String) get(10);
    }

    /**
     * Setter for <code>public.fact.argumentClusterId</code>.
     */
    @Override
    public void setArgumentclusterid(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>public.fact.argumentClusterId</code>.
     */
    @Override
    public String getArgumentclusterid() {
        return (String) get(11);
    }

    /**
     * Setter for <code>public.fact.argumentBadlyTranslatedString</code>.
     */
    @Override
    public void setArgumentbadlytranslatedstring(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>public.fact.argumentBadlyTranslatedString</code>.
     */
    @Override
    public String getArgumentbadlytranslatedstring() {
        return (String) get(12);
    }

    /**
     * Setter for <code>public.fact.rKind</code>.
     */
    @Override
    public void setRkind(String value) {
        set(13, value);
    }

    /**
     * Getter for <code>public.fact.rKind</code>.
     */
    @Override
    public String getRkind() {
        return (String) get(13);
    }

    /**
     * Setter for <code>public.fact.rNistName</code>.
     */
    @Override
    public void setRnistname(String value) {
        set(14, value);
    }

    /**
     * Getter for <code>public.fact.rNistName</code>.
     */
    @Override
    public String getRnistname() {
        return (String) get(14);
    }

    /**
     * Setter for <code>public.fact.resolvedName</code>.
     */
    @Override
    public void setResolvedname(String value) {
        set(15, value);
    }

    /**
     * Getter for <code>public.fact.resolvedName</code>.
     */
    @Override
    public String getResolvedname() {
        return (String) get(15);
    }

    /**
     * Setter for <code>public.fact.resolvedType</code>.
     */
    @Override
    public void setResolvedtype(String value) {
        set(16, value);
    }

    /**
     * Getter for <code>public.fact.resolvedType</code>.
     */
    @Override
    public String getResolvedtype() {
        return (String) get(16);
    }

    /**
     * Setter for <code>public.fact.score</code>.
     */
    @Override
    public void setScore(Double value) {
        set(17, value);
    }

    /**
     * Getter for <code>public.fact.score</code>.
     */
    @Override
    public Double getScore() {
        return (Double) get(17);
    }

    /**
     * Setter for <code>public.fact.fromFuzzyMatching</code>.
     */
    @Override
    public void setFromfuzzymatching(Boolean value) {
        set(18, value);
    }

    /**
     * Getter for <code>public.fact.fromFuzzyMatching</code>.
     */
    @Override
    public Boolean getFromfuzzymatching() {
        return (Boolean) get(18);
    }

    // -------------------------------------------------------------------------
    // Record19 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row19<String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, Double, Boolean> fieldsRow() {
        return (Row19) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row19<String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, Double, Boolean> valuesRow() {
        return (Row19) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return Fact.FACT.MID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return Fact.FACT.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return Fact.FACT.NISTTYPELEFT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return Fact.FACT.NISTTYPERIGHT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return Fact.FACT.NISTTYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return Fact.FACT.TREE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field7() {
        return Fact.FACT.PARTIALLABEL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return Fact.FACT.NISTFULLLABEL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return Fact.FACT.ARGUMENTID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field10() {
        return Fact.FACT.ARGUMENTNISTTYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field11() {
        return Fact.FACT.ARGUMENTRAWSTRING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field12() {
        return Fact.FACT.ARGUMENTCLUSTERID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field13() {
        return Fact.FACT.ARGUMENTBADLYTRANSLATEDSTRING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field14() {
        return Fact.FACT.RKIND;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field15() {
        return Fact.FACT.RNISTNAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field16() {
        return Fact.FACT.RESOLVEDNAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field17() {
        return Fact.FACT.RESOLVEDTYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Double> field18() {
        return Fact.FACT.SCORE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field19() {
        return Fact.FACT.FROMFUZZYMATCHING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component1() {
        return getMid();
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
        return getNisttypeleft();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component4() {
        return getNisttyperight();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component5() {
        return getNisttype();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component6() {
        return getTreeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component7() {
        return getPartiallabel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component8() {
        return getNistfulllabel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component9() {
        return getArgumentid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component10() {
        return getArgumentnisttype();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component11() {
        return getArgumentrawstring();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component12() {
        return getArgumentclusterid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component13() {
        return getArgumentbadlytranslatedstring();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component14() {
        return getRkind();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component15() {
        return getRnistname();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component16() {
        return getResolvedname();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component17() {
        return getResolvedtype();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double component18() {
        return getScore();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean component19() {
        return getFromfuzzymatching();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getMid();
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
        return getNisttypeleft();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getNisttyperight();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getNisttype();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getTreeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value7() {
        return getPartiallabel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value8() {
        return getNistfulllabel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value9() {
        return getArgumentid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value10() {
        return getArgumentnisttype();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value11() {
        return getArgumentrawstring();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value12() {
        return getArgumentclusterid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value13() {
        return getArgumentbadlytranslatedstring();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value14() {
        return getRkind();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value15() {
        return getRnistname();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value16() {
        return getResolvedname();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value17() {
        return getResolvedtype();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double value18() {
        return getScore();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value19() {
        return getFromfuzzymatching();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FactRecord value1(String value) {
        setMid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FactRecord value2(String value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FactRecord value3(String value) {
        setNisttypeleft(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FactRecord value4(String value) {
        setNisttyperight(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FactRecord value5(String value) {
        setNisttype(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FactRecord value6(String value) {
        setTreeId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FactRecord value7(String value) {
        setPartiallabel(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FactRecord value8(String value) {
        setNistfulllabel(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FactRecord value9(String value) {
        setArgumentid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FactRecord value10(String value) {
        setArgumentnisttype(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FactRecord value11(String value) {
        setArgumentrawstring(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FactRecord value12(String value) {
        setArgumentclusterid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FactRecord value13(String value) {
        setArgumentbadlytranslatedstring(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FactRecord value14(String value) {
        setRkind(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FactRecord value15(String value) {
        setRnistname(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FactRecord value16(String value) {
        setResolvedname(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FactRecord value17(String value) {
        setResolvedtype(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FactRecord value18(Double value) {
        setScore(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FactRecord value19(Boolean value) {
        setFromfuzzymatching(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FactRecord values(String value1, String value2, String value3, String value4, String value5, String value6, String value7, String value8, String value9, String value10, String value11, String value12, String value13, String value14, String value15, String value16, String value17, Double value18, Boolean value19) {
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
        value17(value17);
        value18(value18);
        value19(value19);
        return this;
    }

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public void from(IFact from) {
        setMid(from.getMid());
        setId(from.getId());
        setNisttypeleft(from.getNisttypeleft());
        setNisttyperight(from.getNisttyperight());
        setNisttype(from.getNisttype());
        setTreeId(from.getTreeId());
        setPartiallabel(from.getPartiallabel());
        setNistfulllabel(from.getNistfulllabel());
        setArgumentid(from.getArgumentid());
        setArgumentnisttype(from.getArgumentnisttype());
        setArgumentrawstring(from.getArgumentrawstring());
        setArgumentclusterid(from.getArgumentclusterid());
        setArgumentbadlytranslatedstring(from.getArgumentbadlytranslatedstring());
        setRkind(from.getRkind());
        setRnistname(from.getRnistname());
        setResolvedname(from.getResolvedname());
        setResolvedtype(from.getResolvedtype());
        setScore(from.getScore());
        setFromfuzzymatching(from.getFromfuzzymatching());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E extends IFact> E into(E into) {
        into.from(this);
        return into;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached FactRecord
     */
    public FactRecord() {
        super(Fact.FACT);
    }

    /**
     * Create a detached, initialised FactRecord
     */
    public FactRecord(String mid, String id, String nisttypeleft, String nisttyperight, String nisttype, String treeId, String partiallabel, String nistfulllabel, String argumentid, String argumentnisttype, String argumentrawstring, String argumentclusterid, String argumentbadlytranslatedstring, String rkind, String rnistname, String resolvedname, String resolvedtype, Double score, Boolean fromfuzzymatching) {
        super(Fact.FACT);

        set(0, mid);
        set(1, id);
        set(2, nisttypeleft);
        set(3, nisttyperight);
        set(4, nisttype);
        set(5, treeId);
        set(6, partiallabel);
        set(7, nistfulllabel);
        set(8, argumentid);
        set(9, argumentnisttype);
        set(10, argumentrawstring);
        set(11, argumentclusterid);
        set(12, argumentbadlytranslatedstring);
        set(13, rkind);
        set(14, rnistname);
        set(15, resolvedname);
        set(16, resolvedtype);
        set(17, score);
        set(18, fromfuzzymatching);
    }
}
