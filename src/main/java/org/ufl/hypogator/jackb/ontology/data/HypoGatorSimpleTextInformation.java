package org.ufl.hypogator.jackb.ontology.data;

import org.ufl.hypogator.jackb.streamutils.data.Tuple;

public class HypoGatorSimpleTextInformation {
    public String provenance;
    public String text_string;
    public String justification;

    public HypoGatorSimpleTextInformation(Tuple information) {
        provenance = information.get("provenance").getAtomAsString();
        text_string = information.get("text_string").getAtomAsString();
        justification = information.get("justification").getAtomAsString();
    }

    @Override
    public String toString() {
        return "Text{" +
                "provenance='" + provenance + '\'' +
                ", text_string='" + text_string + '\'' +
                ", justification='" + justification + '\'' +
                '}';
    }
}
