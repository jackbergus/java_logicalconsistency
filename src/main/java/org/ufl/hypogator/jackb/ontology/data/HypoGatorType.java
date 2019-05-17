package org.ufl.hypogator.jackb.ontology.data;

import org.ufl.hypogator.jackb.streamutils.data.Tuple;

public class HypoGatorType {
    public String type;
    public String subType;

    public HypoGatorType(Tuple t) {
        type = t.get("type").getAtomAsString();
        subType = t.get("subtype").getAtomAsString();
    }

    @Override
    public String toString() {
        return "HypoGatorType{" +
                "type='" + type + '\'' +
                ", subType='" + subType + '\'' +
                '}';
    }
}
