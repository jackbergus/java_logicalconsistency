/*
 * MultiConceptScraper.java
 * This file is part of scraper
 *
 * Copyright (C) 2018 giacomo
 *
 * scraper is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * scraper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with scraper. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.ufl.hypogator.jackb.scraper;

import com.google.common.collect.HashMultimap;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.ConceptNetVocabulary;
import org.ufl.hypogator.jackb.fuzzymatching.FuzzyMatcher;
import org.ufl.hypogator.jackb.fuzzymatching.TwoGramIndexerJNI;
import org.ufl.hypogator.jackb.traversers.babelnet.BabelNetTraverser;
import org.ufl.hypogator.jackb.m9.configuration.Concept5ClientConfigurations;
import org.ufl.hypogator.jackb.traversers.conceptnet.ConceptNet5Postgres;
import org.ufl.hypogator.jackb.traversers.conceptnet.ConceptNetTraverser;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.EdgeVertex;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.relationships.raw_type.RelationshipTypes;
import org.ufl.hypogator.jackb.scraper.adt.SemanticNetworkTraversers;
import it.uniroma1.lcl.babelnet.BabelSynset;
import it.uniroma1.lcl.babelnet.data.BabelPointer;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * This class combines multiple ConceptScrapers, in oder to reconstruct the hierarchy from multiple Concepts terms
 */
public class MultiConceptScraper<DataSpecificRelationships> {
    final String qdmapFolder;
    final boolean onlyEnglishConcept;
    final int getLimit;
    final SemanticNetworkTraversers<DataSpecificRelationships> snt;
    final TwoGramIndexerJNI server = TwoGramIndexerJNI.getInstance();

    public MultiConceptScraper(String qdmapFolder, boolean onlyEnglishConcept, int getLimit, SemanticNetworkTraversers<DataSpecificRelationships> snt) {
        this.qdmapFolder = qdmapFolder;
        //this.treeFolder = treeFolder;
        this.onlyEnglishConcept = onlyEnglishConcept;
        this.getLimit = getLimit;
        this.snt = snt;
    }

    public MultiConceptScraper(SemanticNetworkTraversers<DataSpecificRelationships> snt) {
        Concept5ClientConfigurations conf = Concept5ClientConfigurations.instantiate();
        this.qdmapFolder = conf.getHierarchiesFolder();
        this.onlyEnglishConcept = conf.retrieveOnlyEnglishConcepts();
        this.getLimit = conf.getLimit();
        this.snt = snt;
    }

    /**
     * Traversing ConceptNet
     * @return
     */
    public static MultiConceptScraper<RelationshipTypes> conceptNetScraper() {
        return new MultiConceptScraper<>(new ConceptNetTraverser(Concept5ClientConfigurations.instantiate(), ConceptNetVocabulary.readDefaultVocabulary()));
    }

    /**
     * Traversing BabelNet
     * @return
     */
    public static MultiConceptScraper<BabelPointer> babelNetScraper() {
        return new MultiConceptScraper<>(new BabelNetTraverser());
    }

    public void multiScrape(File conceptFiles) throws IOException {
        multiScrape(Files.readAllLines(conceptFiles.toPath()));
    }

    public void multiScrape(List<String> conceptFiles) {
        HashMultimap<Pair<String, Boolean>, String> mapScraper = HashMultimap.create();
        for (String line : conceptFiles) {
            String[] type_to_types = line.split(":");
            String[] typetoBool = type_to_types[0].split("=");
            String hierarchyType = typetoBool[0];
            Boolean merge = Boolean.valueOf(typetoBool[1].trim());
            Pair<String, Boolean> cp = new Pair<>(hierarchyType.trim(), merge);
            for (String y : type_to_types[1].split(",")) {
                mapScraper.put(cp, y.trim());
            }
        }

        int count = 0;
        int n = mapScraper.asMap().size();
        for (Map.Entry<Pair<String, Boolean>, Collection<String>> x : mapScraper.asMap().entrySet()) {
            System.out.println("Scraped concepts: " + (count++) + "/" + n + ". \t\t Current:" + x.getKey().getKey());
            boolean doMerge = x.getKey().getValue();
            File qdmf = new File(qdmapFolder, x.getKey().getKey() + "_map.json");
            ConceptScraper3<DataSpecificRelationships> scraper2 = new ConceptScraper3<>(snt, qdmf, /*tree,*/ onlyEnglishConcept, getLimit, null);
            for (String y : x.getValue()) {
                scraper2.setDimension(y);
                if (doMerge) {
                    SemanticNetworkEntryPoint terms = snt.resolveTerm(y);
                    SemanticNetworkEntryPoint defaultRoot = SemanticNetworkEntryPoint.generateDefaultRoot(x.getKey().getKey());

                    switch (terms.getGeneratingSource()) {
                        case BABELNET:
                            // BabelNet provides for the same element multiple possible entrypoints
                            //defaultRoot = terms;
                            for (BabelSynset id : terms.asSynset()) {
                                EdgeVertex ev = ((EdgeVertex) terms).parent.toVertex(id);
                                scraper2.forceTrue(defaultRoot, ev);
                                scraper2.pushQueryStart(ev, ev);
                                scraper2.scrape();
                            }
                            scraper2.forceTrue(terms, defaultRoot);
                            break;
                        case CONCEPTNET:
                            // Conceptnet either finds a perfect match, or no match for its entry point
                            defaultRoot = SemanticNetworkEntryPoint.generateDefaultRoot(x.getKey().getKey());
                            scraper2.forceTrue(defaultRoot, terms);
                            scraper2.pushQueryStart(terms, terms);
                            scraper2.scrape();
                            break;
                        case AIDA:
                            // noop
                            break;
                    }
                }
            }
            scraper2.close();
        }
    }

    public class PrivateTermScorer implements TermScorer<SemanticNetworkEntryPoint> {
        /**
         * The object associated to the current dimension. Everything in the scorer is associated in here
         */
        final ConceptScraper3 scraper2;
        private String dimension;
        private boolean useJNIDump;

        public PrivateTermScorer(String dimension, boolean useJNIDump) {
            this.dimension = dimension;
            this.useJNIDump = useJNIDump;
            if (useJNIDump) {
                TwoGramIndexerJNI.TwoGramIndexerForDimension result = null;
                synchronized (server) {
                    result = server.openDimension(dimension);
                }
                scraper2 = new ConceptScraper3<>(snt, result, false, getLimit, dimension);
            } else {
                scraper2 = new ConceptScraper3<>(snt, new File(qdmapFolder, dimension + "_map.json"), false, getLimit, dimension);
            }
        }

        @Override
        public double score(SemanticNetworkEntryPoint root, SemanticNetworkEntryPoint elem) {
            return scraper2.score(root, elem, true, null);
        }

        @Override
        public Pair<Double, List<SemanticNetworkEntryPoint>> scoreWithPath(SemanticNetworkEntryPoint root, SemanticNetworkEntryPoint elem) {
            return scraper2.scoreWithPath(root, elem);
        }

        @Override
        public FuzzyMatcher<ConceptNet5Postgres.RecordResultForSingleNode> getEnrichedVocabulary() {
            return scraper2.getEnrichedVocabulary();
        }

        @Override
        public String getDimension() {
            return scraper2.getDimension();
        }

        public void close() {
            if (useJNIDump) synchronized (server) {
                server.closeDimension(dimension);
            }
        }

        @Override
        protected void finalize() {
            if (useJNIDump) synchronized (server) {
                server.closeDimension(dimension);
            }
        }
    }

    public PrivateTermScorer dimension(String dimension, boolean useJNIDump) {
        return new PrivateTermScorer(dimension, useJNIDump);
    }

   /*
    * @param dimension
    * @return scorer associated to the dimension
    *
    *public TermScorer<SemanticNetworkEntryPoint> dimension(String dimension) {
        return new TermScorer<SemanticNetworkEntryPoint>() {
            final ConceptScraper3 =  new ConceptScraper3<>(snt, new File(qdmapFolder, dimension + "_map.json"), false, getLimit, dimension);

            @Override
            public double score(SemanticNetworkEntryPoint root, SemanticNetworkEntryPoint elem) {
                return scraper2.score(root, elem, true, null);
            }

            @Override
            public Pair<Double, List<SemanticNetworkEntryPoint>> scoreWithPath(SemanticNetworkEntryPoint root, SemanticNetworkEntryPoint elem) {
                return scraper2.scoreWithPath(root, elem);
            }

            @Override
            public FuzzyMatcher<ConceptNet5Postgres.RecordResultForSingleNode> getEnrichedVocabulary() {
                return scraper2.getEnrichedVocabulary();
            }

            @Override
            public String getDimension() {
                return scraper2.getDimension();
            }
        };
    }*/


}
