package org.ufl.hypogator.jackb.inconsistency;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class AgileField {
    public final String fieldName;
    public final String fieldType;
    public String fieldString;
    public final boolean typeFromFuzzyMatch;
    public final String mid;

    @JsonCreator
    public AgileField(@JsonProperty("fieldName") String fieldName,
                      @JsonProperty("fieldType") String fieldType,
                      @JsonProperty("fieldString") String fieldString,
                      @JsonProperty("typeFromFuzzyMatch") boolean typeFromFuzzyMatch,
                      @JsonProperty("mid") String mid) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.fieldString = fieldString.toLowerCase();
        this.typeFromFuzzyMatch = typeFromFuzzyMatch;
        this.mid = mid;
    }

    @Override
    public String toString() {
        return fieldName+" = "+fieldString+":"+fieldType+" ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgileField that = (AgileField) o;
        return Objects.equals(fieldName, that.fieldName) &&
                Objects.equals(fieldType, that.fieldType) &&
                Objects.equals(fieldString, that.fieldString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldName, fieldType, fieldString);
    }
}
