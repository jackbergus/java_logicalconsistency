package it.giacomobergami.m18.newconceptdisambiguation;

import org.ufl.hypogator.jackb.disambiguation.dimension.concept.DisambiguatorForDimensionForConcept;
import org.ufl.hypogator.jackb.scraper.SemanticNetworkEntryPoint;

public class TestingConceptNet {

    public static void main(String[] args) {
        //InformativeConcept result = DimConceptFactory.getInstance(true).apply("Union").disambiguate("sniper");
        SemanticNetworkEntryPoint result = new DisambiguatorForDimensionForConcept("Union", new String[]{"partOf"}).resolveExactTerm("person");
        System.out.println(result);
    }

}
