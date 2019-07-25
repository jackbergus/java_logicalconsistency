package org.ufl.hypogator.jackb.main;

import org.ufl.hypogator.jackb.ConfigurationEntrypoint;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.ConceptNetVocabulary;
import org.ufl.hypogator.jackb.scraper.MultiConceptScraper;
import org.ufl.hypogator.jackb.traversers.conceptnet.Concept5ClientConfigurations;
import org.ufl.hypogator.jackb.traversers.conceptnet.ConceptNetJNITraverser;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.relationships.raw_type.RelationshipTypes;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class CSVDimensionDumps {

    public static void main(String[] args) throws IOException {
        ConceptNetJNITraverser traverser = ConceptNetJNITraverser.getInstance();
        MultiConceptScraper<RelationshipTypes> scratcher = new MultiConceptScraper<>(traverser);
        File hf = new File(Concept5ClientConfigurations.instantiate().getHierarchiesFolder());
        for (File file : Objects.requireNonNull(hf.listFiles())) {
            if (file.isFile() && file.getName().endsWith("_map.json")) {
                String dimension = file.getName().replace("_map.json", "");
                System.out.println("Serializing dimension: "+dimension);
                ((ConceptNetVocabulary) scratcher.dimension(dimension, false).getEnrichedVocabulary())
                        .serializeToFolder(new File(hf, dimension+"_csvDir"), traverser.dumpingGround);
            }
        }
    }

}
