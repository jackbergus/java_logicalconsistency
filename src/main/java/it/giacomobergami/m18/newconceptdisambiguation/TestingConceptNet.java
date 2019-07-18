package it.giacomobergami.m18.newconceptdisambiguation;

import org.ufl.hypogator.jackb.disambiguation.dimension.concept.DimConceptFactory;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.DisambiguatorForDimensionForConcept;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.InformativeConcept;
import org.ufl.hypogator.jackb.scraper.SemanticNetworkEntryPoint;

public class TestingConceptNet {

    public static void main(String[] args) {
        SemanticNetworkEntryPoint result = new DisambiguatorForDimensionForConcept("Union", new String[]{"partOf"}).resolveExactTerm("person");
        System.out.println(result);
    }

}
