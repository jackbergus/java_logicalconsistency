package org.ufl.hypogator.jackb.ontology;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class Ontology {
    @JsonProperty("Entity")
    public  Sort entity;

    @JsonProperty("Filler")
    public  Sort filler;

    @JsonProperty("Relation")
    public  Sort relation;

    @JsonProperty("Event")
    public  Sort event;

    public Sort[] sorts = null;
    public Sort[] getSorts() {
        if (sorts == null)
            sorts = new Sort[]{entity, filler, relation, event};
        return sorts;
    }
}