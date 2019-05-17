package org.ufl.hypogator.jackb.ontology.data;

import org.ufl.hypogator.jackb.fuzzymatching.ldc.LDCResult;

import java.util.Objects;

/**
 * A mention is a typed string, that is a string associated to a type
 */
public class StringMention implements TypedValue {
    /**
     * Type associated to the string
     */
    private String type;

    /**
     * Actual string
     */
    private final LDCResult str;
    private boolean fromFuzzyMatch;

    public StringMention(String type, String str) {
        this.type = type;
        this.str = new LDCResult(str);
        this.fromFuzzyMatch = false;
    }

    @Override
    public LDCResult value() {
        return str;
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public void setTypeComingFromFuzzyMatch() {
        this.fromFuzzyMatch = true;
    }

    @Override
    public boolean doesTypeComesFromFuzzyMatch() {
        return fromFuzzyMatch;
    }

    @Override
    public void setType(String nistType) {
        this.type = nistType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringMention that = (StringMention) o;
        return Objects.equals(str, that.str);
    }

    @Override
    public int hashCode() {
        return Objects.hash(str);
    }
}
