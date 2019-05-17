package org.ufl.hypogator.jackb.ontology.data;

import org.ufl.hypogator.jackb.fuzzymatching.ldc.LDCResult;

public interface TypedValue {
    LDCResult value();
    String type();
    void setTypeComingFromFuzzyMatch();
    boolean doesTypeComesFromFuzzyMatch();
    void setType(String nistType);
}
