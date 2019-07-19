package it.giacomobergami.m18.newconceptdisambiguation;

import org.ufl.hypogator.jackb.comparators.partialOrders.PartialOrderComparison;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.ComparingConceptResolution;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.DimConceptsUnion;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.DisambiguatorForDimensionForConcept;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.InformativeConcept;
import org.ufl.hypogator.jackb.scraper.SemanticNetworkEntryPoint;

public class TestingConceptNet {

    public static void main(String[] args) {
        //InformativeConcept result = DimConceptFactory.getInstance(true).apply("Union").disambiguate("sniper");
        //InformativeConcept result = new DisambiguatorForDimensionForConcept("Union", new String[]{"partOf"}).disambiguate("person");

        PartialOrderComparison result = new DimConceptsUnion("Union").compare("sniper", "shooter");
        System.out.println(result);
    }

}
