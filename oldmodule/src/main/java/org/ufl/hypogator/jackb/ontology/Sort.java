package org.ufl.hypogator.jackb.ontology;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Objects;

/**
 * A sort is a specific kind, to which multiple data are associated
 */
public final class Sort {
    public final String name;
    public final TypeSubtype hasTypes[];

    @JsonCreator
    public Sort(@JsonProperty("name") String name, @JsonProperty("hasTypes") TypeSubtype[] hasTypes){
        this.name = name;
        this.hasTypes = hasTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sort sort = (Sort) o;
        return Objects.equals(name, sort.name) &&
                Arrays.equals(hasTypes, sort.hasTypes);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name);
        result = 31 * result + Arrays.hashCode(hasTypes);
        return result;
    }
}