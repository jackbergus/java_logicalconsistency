/*
 * This file is generated by jOOQ.
 */
package org.ufl.aida.ta2.tables.pojos;


import java.util.Arrays;

import javax.annotation.Generated;

import org.ufl.aida.ta2.tables.interfaces.ITuples;


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
public class Tuples implements ITuples {

    private static final long serialVersionUID = 614646872;

    private String   nisttype;
    private String   mid;
    private Double   score;
    private Boolean  negated;
    private Boolean  hedged;
    private String[] constituent;
    private Object[] arrayAgg;

    public Tuples() {}

    public Tuples(ITuples value) {
        this.nisttype = value.getNisttype();
        this.mid = value.getMid();
        this.score = value.getScore();
        this.negated = value.getNegated();
        this.hedged = value.getHedged();
        this.constituent = value.getConstituent();
        this.arrayAgg = value.getArrayAgg();
    }

    public Tuples(
        String   nisttype,
        String   mid,
        Double   score,
        Boolean  negated,
        Boolean  hedged,
        String[] constituent,
        Object[] arrayAgg
    ) {
        this.nisttype = nisttype;
        this.mid = mid;
        this.score = score;
        this.negated = negated;
        this.hedged = hedged;
        this.constituent = constituent;
        this.arrayAgg = arrayAgg;
    }

    @Override
    public String getNisttype() {
        return this.nisttype;
    }

    @Override
    public void setNisttype(String nisttype) {
        this.nisttype = nisttype;
    }

    @Override
    public String getMid() {
        return this.mid;
    }

    @Override
    public void setMid(String mid) {
        this.mid = mid;
    }

    @Override
    public Double getScore() {
        return this.score;
    }

    @Override
    public void setScore(Double score) {
        this.score = score;
    }

    @Override
    public Boolean getNegated() {
        return this.negated;
    }

    @Override
    public void setNegated(Boolean negated) {
        this.negated = negated;
    }

    @Override
    public Boolean getHedged() {
        return this.hedged;
    }

    @Override
    public void setHedged(Boolean hedged) {
        this.hedged = hedged;
    }

    @Override
    public String[] getConstituent() {
        return this.constituent;
    }

    @Override
    public void setConstituent(String... constituent) {
        this.constituent = constituent;
    }

    @Override
    public Object[] getArrayAgg() {
        return this.arrayAgg;
    }

    @Override
    public void setArrayAgg(Object... arrayAgg) {
        this.arrayAgg = arrayAgg;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Tuples other = (Tuples) obj;
        if (nisttype == null) {
            if (other.nisttype != null)
                return false;
        }
        else if (!nisttype.equals(other.nisttype))
            return false;
        if (mid == null) {
            if (other.mid != null)
                return false;
        }
        else if (!mid.equals(other.mid))
            return false;
        if (score == null) {
            if (other.score != null)
                return false;
        }
        else if (!score.equals(other.score))
            return false;
        if (negated == null) {
            if (other.negated != null)
                return false;
        }
        else if (!negated.equals(other.negated))
            return false;
        if (hedged == null) {
            if (other.hedged != null)
                return false;
        }
        else if (!hedged.equals(other.hedged))
            return false;
        if (constituent == null) {
            if (other.constituent != null)
                return false;
        }
        else if (!Arrays.equals(constituent, other.constituent))
            return false;
        if (arrayAgg == null) {
            if (other.arrayAgg != null)
                return false;
        }
        else if (!Arrays.equals(arrayAgg, other.arrayAgg))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.nisttype == null) ? 0 : this.nisttype.hashCode());
        result = prime * result + ((this.mid == null) ? 0 : this.mid.hashCode());
        result = prime * result + ((this.score == null) ? 0 : this.score.hashCode());
        result = prime * result + ((this.negated == null) ? 0 : this.negated.hashCode());
        result = prime * result + ((this.hedged == null) ? 0 : this.hedged.hashCode());
        result = prime * result + ((this.constituent == null) ? 0 : Arrays.hashCode(this.constituent));
        result = prime * result + ((this.arrayAgg == null) ? 0 : Arrays.hashCode(this.arrayAgg));
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Tuples (");

        sb.append(nisttype);
        sb.append(", ").append(mid);
        sb.append(", ").append(score);
        sb.append(", ").append(negated);
        sb.append(", ").append(hedged);
        sb.append(", ").append(Arrays.toString(constituent));
        sb.append(", ").append(Arrays.toString(arrayAgg));

        sb.append(")");
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public void from(ITuples from) {
        setNisttype(from.getNisttype());
        setMid(from.getMid());
        setScore(from.getScore());
        setNegated(from.getNegated());
        setHedged(from.getHedged());
        setConstituent(from.getConstituent());
        setArrayAgg(from.getArrayAgg());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E extends ITuples> E into(E into) {
        into.from(this);
        return into;
    }
}
