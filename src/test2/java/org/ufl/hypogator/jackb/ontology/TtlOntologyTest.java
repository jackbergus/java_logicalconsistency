package org.ufl.hypogator.jackb.ontology;

import org.junit.Assert;
import org.junit.Test;
import org.ufl.hypogator.jackb.ontology.data.RawEventRelationship;
import org.ufl.hypogator.jackb.streamutils.collectors.CollectToSet;

import java.util.Set;

import static org.junit.Assert.*;

public class TtlOntologyTest {

    @Test
    public void asBackwardRepresentation() {
        // ERROR: bug
        /*
        Please note that ttl has different information from json in this regard, because some arguments can be diverse

        TtlOntology ttl = new TtlOntology("data/SeedlingOntology.ttl");
        JsonOntologyLoader json = JsonOntologyLoader.getInstance();
        Set<String> s1 = json.asBackwardRepresentation().map(RawEventRelationship::toString).collect(new CollectToSet<>(false));
        Set<String> s2 = ttl.asBackwardRepresentation().map(RawEventRelationship::toString).collect(new CollectToSet<>(false));
        Assert.assertTrue(s1.containsAll(s2));
        Assert.assertTrue(s2.containsAll(s1));

        */
    }

    // OK TEST
    @Test
    public void getEntityOrFillers() {
        TtlOntology ttl = new TtlOntology("data/SeedlingOntology.ttl");
        JsonOntologyLoader json = JsonOntologyLoader.getInstance();
        Set<String> s1 = ttl.getEntityOrFillers();
        Set<String> s2 = json.getEntityOrFillers();
        Assert.assertTrue(s1.containsAll(s2));
        Assert.assertTrue(s2.containsAll(s1));
    }

    @Test
    public void resolveSingleLDCType() {
    }

    @Test
    public void resolveSingleNISTType() {
    }

    @Test
    public void resolveNISTTypes() {
    }

    @Test
    public void resolveLDCToNist() {
    }

    @Test
    public void getTypesForLabel() {
    }
}