package org.ufl.hypogator.jackb.ontology.data;


import org.ufl.hypogator.jackb.streamutils.data.Tuple;

public class HypoGatorDate {
    public String type;
    public String date;

    public HypoGatorDate(String typeKey, String dateKey, Tuple t) {
        type = t.get(typeKey).getAtomAsString();
        date = t.get(dateKey).getAtomAsString();
    }

    @Override
    public String toString() {
        return "HypoGatorDate{" +
                "type='" + type + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
