package org.ufl.hypogator.jackb;

import org.ufl.hypogator.jackb.comparators.partialOrders.InformationPreservingComparator;
import org.ufl.hypogator.jackb.disambiguation.dimension.Dimension;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.DimConceptFactory;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.DimConcepts;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.InformativeConcept;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.ResolvedConcept;
import org.ufl.hypogator.jackb.ontology.JsonOntologyLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;

public class ConfigurationEntrypoint {

    public final String disambiguationPolicy;
    Properties p;
    public final File ontologyFile;
    public final File postgresDump;
    public final File clavinFolder;
    public final File conceptnetScraper;
    public final Boolean doClavinFuzzyMatch;
    public final Boolean logging;
    public final File geonamesHierarchy;
    public final double threshold;
    public final double accountabilityThreshold;
    public final String fuzzyAlgorithm;
    public final Integer recursiveGraphExpander;
    public final Boolean parallelize;
    public final Boolean forceUnion;
    public final String typingPolicy;
    public final String groupingPolicy;
    public final File functionalDependencyFile;
    public final Function<String, Dimension<ResolvedConcept, InformativeConcept>> dimConceptFactory;

    private static ConfigurationEntrypoint self;
    private ConfigurationEntrypoint() throws IOException {
        p = new Properties();
        p.load(new FileInputStream("logicalinconsistency.properties"));
        ontologyFile = new File(p.getProperty("ontology", "data/ontology.json"));
        functionalDependencyFile = new File(p.getProperty("fdep", "data/fdep.txt"));
        clavinFolder = new File(p.getProperty("clavinIndex", "clavin-lucene-index"));
        doClavinFuzzyMatch = Boolean.valueOf(p.getProperty("fuzzyMatchGeonames", "true"));
        logging = Boolean.valueOf(p.getProperty("logging", "false"));
        threshold = Double.valueOf(p.getProperty("threshold", ".6"));
        geonamesHierarchy = new File(p.getProperty("geonames-hierarchy", "data/geonames_hierarchy.tab"));
        fuzzyAlgorithm = p.getProperty("fuzzyAlgorithm", "MultiWordSimilarity");
        conceptnetScraper = new File(p.getProperty("scrapeConceptNet", "data/scrape_conceptnet_hierarchies.txt"));
        recursiveGraphExpander = Integer.valueOf(p.getProperty("recursiveGraphExpander", "1"));
        accountabilityThreshold = Double.valueOf(p.getProperty("accountabilityThreshold", "0.4"));
        parallelize = Boolean.valueOf(p.getProperty("parallelize", "true"));
        typingPolicy = p.getProperty("typingPolicy", "EqualityApproximation");
        groupingPolicy = p.getProperty("groupingPolicy", "FunctionalDependency_NoFunctionalDependency");
        disambiguationPolicy = p.getProperty("disambiguationPolicy", "MaximumDisambiguationComparator");
        forceUnion = Boolean.valueOf(p.getProperty("forceUnion", "false"));
        postgresDump = new File(p.getProperty("pgDump", "data/postgres"));
        dimConceptFactory = DimConceptFactory.getInstance(Boolean.valueOf(p.getProperty("DimConceptsWithUnion", "false")));
    }

    public static ConfigurationEntrypoint getInstance() {
        if (self == null) {
            try {
                return (self = new ConfigurationEntrypoint());
            } catch (IOException e) {
                return null;
            }
        }
        return self;
    }

    public static Dimension<ResolvedConcept, InformativeConcept>  generateConceptComparatorFromFieldName(String fieldName) {
        return Objects.requireNonNull(getInstance()).dimConceptFactory.apply(fieldName);
    }

    public JsonOntologyLoader getOntology() {
        return JsonOntologyLoader.getInstance();
    }

}
