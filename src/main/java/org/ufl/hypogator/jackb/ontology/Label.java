package org.ufl.hypogator.jackb.ontology;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Objects;

public final class Label {
    @JsonProperty("ldcName")
    public String ldcName;

    @JsonProperty("nistName")
    public String nistName;

    @JsonProperty("fromLDCType")
    public  String fromLDCType;

    @JsonProperty("fromNISTType")
    public  String fromNISTType;

    @JsonProperty("allowedLDCTypes")
    public  String[] allowedLDCTypes;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Label label = (Label) o;
        return Objects.equals(ldcName, label.ldcName) &&
                Objects.equals(nistName, label.nistName) &&
                Objects.equals(fromLDCType, label.fromLDCType) &&
                Objects.equals(fromNISTType, label.fromNISTType) &&
                Arrays.equals(allowedLDCTypes, label.allowedLDCTypes);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(ldcName, nistName, fromLDCType, fromNISTType);
        result = 31 * result + Arrays.hashCode(allowedLDCTypes);
        return result;
    }
}