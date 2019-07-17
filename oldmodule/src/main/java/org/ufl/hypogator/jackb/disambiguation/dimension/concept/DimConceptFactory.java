package org.ufl.hypogator.jackb.disambiguation.dimension.concept;

import org.ufl.hypogator.jackb.disambiguation.dimension.Dimension;

import java.util.HashMap;
import java.util.function.Function;

public class DimConceptFactory {

    private static HashMap<String, DimConceptsUnion> asUnion = new HashMap<>();
    private static HashMap<String, DimConcepts>      asNotUnion = new HashMap<>();

    public static Function<String, Dimension<ResolvedConcept, InformativeConcept>> getInstance(boolean withUnions) {
        return s -> {
            if (withUnions) {
                DimConceptsUnion element = asUnion.get(s);
                if (element == null) {
                    element = new DimConceptsUnion(s);
                    asUnion.put(s, element);
                }
                return element;
            } else {
                DimConcepts element = asNotUnion.get(s);
                if (element == null) {
                    element = new DimConcepts(s);
                    asNotUnion.put(s, element);
                }
                return element;
            }
        };
    }


}
