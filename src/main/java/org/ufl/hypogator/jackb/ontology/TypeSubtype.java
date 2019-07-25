package org.ufl.hypogator.jackb.ontology;

import org.ufl.hypogator.jackb.ontology.data.RawEventRelationship;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Objects;

public final class TypeSubtype {
    @JsonProperty("kind")
    public  String kind;

    @JsonProperty("semanticName")
    public  String nistName;

    @JsonProperty("ldcName")
    public  String ldcName;

    @JsonProperty("ldcDescription")
    public  String ldcDescription;

    @JsonProperty("argumentTypes")
    public  Label argumentTypes[];


    public RawEventRelationship backwardCompatibility() {
        RawEventRelationship rel = new RawEventRelationship();
        rel.setNISTType(kind.toLowerCase(),nistName);
        rel.setLDCType(kind.toLowerCase(),nistName);
        for (int i = 0, n = argumentTypes.length; i <n; i++) {
            rel.put(i, argumentTypes[i].nistName);
        }
        return rel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeSubtype that = (TypeSubtype) o;
        return Objects.equals(kind, that.kind) &&
                Objects.equals(nistName, that.nistName) &&
                Objects.equals(ldcName, that.ldcName) &&
                Objects.equals(ldcDescription, that.ldcDescription) &&
                Arrays.equals(argumentTypes, that.argumentTypes);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(kind, nistName, ldcName, ldcDescription);
        result = 31 * result + Arrays.hashCode(argumentTypes);
        return result;
    }
}