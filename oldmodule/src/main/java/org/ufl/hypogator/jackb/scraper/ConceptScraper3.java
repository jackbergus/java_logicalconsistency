/*
 * ConceptScraper2.java
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

import org.ufl.hypogator.jackb.ConfigurationEntrypoint;
import org.ufl.hypogator.jackb.fuzzymatching.FuzzyMatcher;
import org.ufl.hypogator.jackb.fuzzymatching.TwoGramIndexerJNI;
import org.ufl.hypogator.jackb.logger.Logger;
import org.ufl.hypogator.jackb.logger.LoggerFactory;
import org.ufl.hypogator.jackb.m9.configuration.Concept5ClientConfigurations;
import org.ufl.hypogator.jackb.traversers.conceptnet.ConceptNet5Postgres;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.Edge;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.EdgeVertex;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.relationships.aggregation_1.SemanticEdge;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.relationships.aggregation_2.TraversalEdge;
import org.ufl.hypogator.jackb.scraper.adt.QuadrupleScraper;
import org.ufl.hypogator.jackb.scraper.adt.SemanticNetworkTraversers;
import org.ufl.hypogator.jackb.scraper.algorithms.CheckIsA;
import org.ufl.hypogator.jackb.scraper.algorithms.StoreAndQuery;
import javafx.util.Pair;
import org.ufl.hypogator.jackb.utils.SetOperations;

import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * This class implements the traversal of a ConceptNetwork, independently from its API and data structure.
 * In particular, the class SemanticNetworkEntryPoint provides the description for each node of the traversed
 * network, and SemanticNetworkTraversers is the class that actually implements the traversal of the hierarchy.
 *
 * @param <DataSpecificRelationships>       Relationships associated to the current semantic network of choice
 */
public class ConceptScraper3<DataSpecificRelationships> implements AutoCloseable, TermScorer<SemanticNetworkEntryPoint> {

    private String dimension;
    private final boolean onlyEnglishConcept;
    private final HashSet<String> visitedIdHierarchy = new HashSet<>();
    private final int limit;
    private final FuzzyMatcher<ConceptNet5Postgres.RecordResultForSingleNode> enrichedVocabulary;
    private final static Logger logger = LoggerFactory.getLogger(ConceptScraper3.class);

    // Algorithms
    private final CheckIsA<DataSpecificRelationships> cia;
    private StoreAndQuery saq;
    private Set<String> discardedId;
    private final static double accountabilityThreshold = ConfigurationEntrypoint.getInstance().accountabilityThreshold;

    private SemanticNetworkTraversers<DataSpecificRelationships> cnt;

    /**
     * This concept configuration requires to separately provide the equivalent type
     *
     * @param graphFile
     * @param getLimit limit to similarity traversal
     * @param dimension     type to scrape
     */
    public ConceptScraper3(SemanticNetworkTraversers<DataSpecificRelationships> pg_dump_traverser, File graphFile, boolean onlyEnglishConcept, int getLimit, String dimension) {
        //this.conf = Concept5ClientConfigurations.instantiate();
        this.onlyEnglishConcept = onlyEnglishConcept;
        File scraped = Concept5ClientConfigurations.instantiate().getEntityMultimapFile();
        //boolean doScrape = !scraped.exists();
        this.limit = getLimit;

        //Initialization of CheckIsA: this also allows to load the vocabulary
        cia = new CheckIsA<>(pg_dump_traverser, graphFile, dimension);
        if (dimension != null) {
            this.dimension = dimension;
            saq = cia.genereateStoreAndQuery(pg_dump_traverser.resolveTerm(this.dimension));
        }
        discardedId = new HashSet<>();
        this.cnt = pg_dump_traverser;
        enrichedVocabulary = cia.getEnrichedVocabularyWithHierarchy();
    }

    public ConceptScraper3(SemanticNetworkTraversers<DataSpecificRelationships> pg_dump_traverser, TwoGramIndexerJNI.TwoGramIndexerForDimension graphVoc, boolean onlyEnglishConcept, int getLimit, String dimension) {
        //this.conf = Concept5ClientConfigurations.instantiate();
        this.onlyEnglishConcept = onlyEnglishConcept;
        File scraped = Concept5ClientConfigurations.instantiate().getEntityMultimapFile();
        //boolean doScrape = !scraped.exists();
        this.limit = getLimit;

        //Initialization of CheckIsA: this also allows to load the vocabulary
        cia = new CheckIsA<>(pg_dump_traverser, graphVoc, dimension);
        if (dimension != null) {
            this.dimension = dimension;
            saq = cia.genereateStoreAndQuery(pg_dump_traverser.resolveTerm(this.dimension));
        }
        discardedId = new HashSet<>();
        this.cnt = pg_dump_traverser;
        enrichedVocabulary = cia.getEnrichedVocabularyWithHierarchy();
    }

    public void setDimension(String dimension) {
        saq = cia.genereateStoreAndQuery(cnt.resolveTerm(dimension));
        this.dimension = dimension;
    }

    private static ConceptScraper3 instance;

    private void scrape(SemanticNetworkEntryPoint concept) {
        cia.resetVisitedTerms();
        //TODO: concept = concept.toLowerCase();
        //saq.pushQueryStart(concept);
        discardedId.clear();
        start();
        discardedId.clear();
        cia.resetVisitedTerms();
        saq.close();
    }

    public void scrape() {
        scrape(null/*cnt.resolveTerm(this.type)*/);
    }


    private void start() {
        while (!saq.hasNoQuery()) {
            // Mimicking the recursive call
            QuadrupleScraper query = saq.popQuery();
            SemanticNetworkEntryPoint hierarchyRoot = query.root;
            SemanticNetworkEntryPoint currentHierarchyElement = query.current;

            double penalty = query.probability;
            int count = query.count;

            // Limiting the search to non-stop words
            if (count > limit || enrichedVocabulary.isStopWord(currentHierarchyElement.getValue()))
                continue;
            //System.out.println(query);

            /////////////////////////////
            // Ascending the hierarchy //
            /////////////////////////////

            // If I have already discarded the element, I do not want to visit it twice to get the same result
            if (discardedId.contains(currentHierarchyElement.getSemanticId()))
                continue;

            // I want to check how much such element can be used to further descend the hierarchy to achieve concept
            // element.
            double countDisambiguations = score(hierarchyRoot, currentHierarchyElement, false, null);
            penalty = penalty * countDisambiguations;

            if (penalty <= accountabilityThreshold) {
                discardedId.add(currentHierarchyElement.getSemanticId());
                visitedIdHierarchy.add(currentHierarchyElement.getSemanticId());
                saq.clearDiscarded(discardedId);
                logger.out("discarded: " + currentHierarchyElement);
                continue;
            } else {
                String out = new String(new char[count]).replace("\0", "-") + "->" + currentHierarchyElement.getValue() + " p=" + penalty;
                //tree.putAllPath(map, count);
                logger.out(out);
            }

            if (!visitedIdHierarchy.add(currentHierarchyElement.getSemanticId())) continue;

            // Getting all the elements which are synonyms to the current element.
            for (Edge localEdge : cnt.synonymsOutgoing(currentHierarchyElement)) {
                if (!onlyEnglishConcept || (
                        localEdge.getTarget().getLanguage().equals("en")
                                && localEdge.getSource().getLanguage().equals("en"))) {
                    SemanticEdge semantic = SemanticEdge.fromEdge(localEdge);
                    logger.debug(semantic.getSrc().getValue()+"--?->"+semantic.getDst().getValue());
                    if (!localEdge.getRelationship().isSuitableForHierarchyTraversing())
                        continue;
                    if (!semantic.isNegated()) {
                        TraversalEdge edge = semantic.coarser();
                        saq.storeAndQuery(hierarchyRoot, edge.dst, penalty, count, edge);
                        //logger.debug(edge.dst.getValue()+" coming next...");
                        //saq.storeAndQuery(hierarchyRoot, edge.dst, penalty, count, edge.flip());
                    }
                }
            }

            int i = -1;
            for (SemanticEdge semantic : cnt.similarIngoing(currentHierarchyElement)) {
                TraversalEdge edge = semantic.coarser();
                //System.err.println(edge);
                if (score(hierarchyRoot, edge.src, false, null) >= accountabilityThreshold) {
                    //logger.debug(edge.src.getValue()+" coming next...");
                    saq.storeAndQueryEquivalenceClass(hierarchyRoot, currentHierarchyElement, edge.src, penalty * 0.9, count + 2, edge);
                    saq.storeAndQuery(hierarchyRoot, edge.src, penalty * 0.9, count + 2, edge);
                } else {
                    discardedId.add(currentHierarchyElement.getSemanticId());
                    visitedIdHierarchy.add(currentHierarchyElement.getSemanticId());
                    saq.clearDiscarded(discardedId);
                }
            }

            //////////////////////////////
            // Descending the hierarchy //
            //////////////////////////////
            double d = 0;
            for (SemanticEdge semantic : cnt.descendHierarchy(currentHierarchyElement)/*sfv.streamForValues((x, y) -> x.ingoingRelDefaultEn(currentHierarchyElement, y), false, RelationshipTypes.InstanceOf, RelationshipTypes.IsA, RelationshipTypes.genre, RelationshipTypes.genus, RelationshipTypes.PartOf)*/) {
                if (score(hierarchyRoot, semantic.src, false, null) >= accountabilityThreshold) {
                    //logger.debug(semantic.src.getValue()+" coming next...");
                    saq.storeAndQuery(hierarchyRoot, semantic.src, penalty, count + 1, semantic.coarser());
                    //System.out.println(semantic.src+" approved");
                } else {
                    //System.out.println(semantic.src+" discarded");
                    discardedId.add(semantic.src.getSemanticId());
                    visitedIdHierarchy.add(semantic.src.getSemanticId());
                    saq.clearDiscarded(discardedId);
                }
            }
        }
    }

    @Override
    public double score(SemanticNetworkEntryPoint root, SemanticNetworkEntryPoint element) {
        return score(root, element, false, null);
    }

    /**
     * 1) The main function used to perform the score for the inference part
     * @param root  Root to be reached during the traversal operation
     * @param elem  Initial element from which start the inference chain
     * @return      Whehter the two elements are related within the current type information or not.
     */
    @Override
    public Pair<Double, List<SemanticNetworkEntryPoint>> scoreWithPath(SemanticNetworkEntryPoint root, SemanticNetworkEntryPoint elem) {
        List<SemanticNetworkEntryPoint> s0 = new ArrayList<>();
        double score = score(root, elem, true, s0);
        /* XXX: this statement avoids to do graph expansion
        if (score > 0) s0.add(root);*/
        s0.add(root);
        if (!s0.isEmpty()) {
            if (!s0.contains(elem)) s0.add(0, elem);
        }
        s0 = SetOperations.removeDuplicatesFromList(s0);
        if (s0.contains(root)) {
            return new Pair<>(score, s0.subList(0, s0.indexOf(root) + 1));
        }
        return new Pair<>(score, s0);
    }

    @Override
    public FuzzyMatcher<ConceptNet5Postgres.RecordResultForSingleNode> getEnrichedVocabulary() {
        return this.enrichedVocabulary;
    }

    /**
     * 2) Second method to be invoked. This method performs the actual path navigation
     *
     * This method is invoked while performing the top-down hierarchy creation from Concepts. It is used to check
     * whether a given type can be related to a possible root type, by traversing the hiearchy bottom-up
     *
     * @param hierarchyRoot
     * @param currentHierarchyElement
     * @param alreadyContains
     * @return
     */
    public double score(SemanticNetworkEntryPoint hierarchyRoot, SemanticNetworkEntryPoint currentHierarchyElement, boolean alreadyContains, List<SemanticNetworkEntryPoint> path) {
        double countDisambiguations;
        Set<List<SemanticNetworkEntryPoint>> sls = new HashSet<>();
        HashMap<List<SemanticNetworkEntryPoint>, Double> map = new HashMap<>();

        if (hierarchyRoot.isStopPointFor(currentHierarchyElement)) {
            //totalTop = 1.0;
            if (path != null) path.add(hierarchyRoot);
            countDisambiguations = 1.0;
        } else {
            // Preliminary check all the elements within the hierarchy
            if (alreadyContains && cia.containsVertex(currentHierarchyElement)) {
                return cia.completeExisting(currentHierarchyElement, hierarchyRoot, path) ? 1.0 : 0.0;
                //return 1.0;
            } //else...

            countDisambiguations = 1.0;
            double all = 0;
            double countOk = 0;
            for (SemanticEdge te : cnt.getSemanticUpwardEdges(currentHierarchyElement)) {

                List<SemanticNetworkEntryPoint> al = path == null ? null : new ArrayList<>(path);

                // This method call performs the effective recursive call to score
                // Please note that "invoke" automatically increments countOk and multiplies countDisambiguations for the inverse of the disjunction (negated conjunctions)
                CountDisambiguations countDisambiguations1 = new CountDisambiguations(hierarchyRoot,
                        alreadyContains, countDisambiguations, countOk, te.dst,
                        1.0, true).invoke(al);
                if (al != null) {
                    sls.add(al);
                    map.put(al, countDisambiguations1.getCountDisambiguations());
                }

                countDisambiguations = countDisambiguations1.getCountDisambiguations();
                countOk = countDisambiguations1.getCountOk();
                all++;
            }

            // If I was not able to find an is-a edge, I'm going to approximate with a relatedTo edge
            if (all == 0) {
                countDisambiguations = 1.0; // ERROR: it should work with 1.0
                List<Pair<Boolean, SemanticEdge>> se = new ArrayList<>();

                cnt.getRelatedEdges(currentHierarchyElement, true)
                        .forEach(x -> se.add(new Pair<>(true, x)));

                cnt.getRelatedEdges(currentHierarchyElement, false)
                        .forEach(x -> se.add(new Pair<>(false, x)));
                boolean morePreciseGet = false;

                for (Pair<Boolean, SemanticEdge> ex : se) {
                    SemanticNetworkEntryPoint label = ex.getKey() ? ex.getValue().dst : ex.getValue().src;
                    if (alreadyContains && this.saq.keyExists(label)) {
                        morePreciseGet = true;
                        List<SemanticNetworkEntryPoint> al = path == null ? null : new ArrayList<>(path);
                        this.cia.completeExisting(label, hierarchyRoot, al);
                        if (al != null) {
                            sls.add(al);
                            map.put(al, .9);
                        }
                    }
                }

                if (!morePreciseGet) {
                    for (SemanticEdge te : cnt.getRelatedEdges(currentHierarchyElement, true)) {
                        List<SemanticNetworkEntryPoint> al = path == null ? null : new ArrayList<>(path);

                        // This method call performs the effective recursive call to score
                        CountDisambiguations countDisambiguations1 = new CountDisambiguations(hierarchyRoot,
                                alreadyContains, countDisambiguations, countOk, te.dst,
                                .9, false).invoke(al);
                        if (al != null) {
                            sls.add(al);
                            map.put(al, countDisambiguations1.getCountDisambiguations());
                        }

                        countDisambiguations = countDisambiguations1.getCountDisambiguations();
                        countOk = countDisambiguations1.getCountOk();
                        all++;
                    }
                    for (SemanticEdge te : cnt.getRelatedEdges(currentHierarchyElement, false)) {
                        List<SemanticNetworkEntryPoint> al = path == null ? null : new ArrayList<>(path);

                        // This method call performs the effective recursive call to score
                        CountDisambiguations countDisambiguations1 = new CountDisambiguations(hierarchyRoot,
                                alreadyContains, countDisambiguations, countOk, te.src,
                                .9, false).invoke(al);
                        if (al != null) {
                            sls.add(al);
                            map.put(al, countDisambiguations1.getCountDisambiguations());
                        }

                        countDisambiguations = countDisambiguations1.getCountDisambiguations();
                        countOk = countDisambiguations1.getCountOk();
                        all++;
                    }
                }

                // If I have no perfect matches, everything can be a possible solution
                if (sls.size() <= 0 || !morePreciseGet) {
                    countDisambiguations = 1.0 - countDisambiguations;
                }

            } else {
                // If I have a hierarchy, I have to weight the disjunctive probability
                // with the actual OK hierarchy elements
                countDisambiguations = (1.0 - countDisambiguations) * (countOk / all);
            }

            if (countOk == 0) {
                discardedId.add(currentHierarchyElement.getSemanticId());
                visitedIdHierarchy.add(currentHierarchyElement.getSemanticId());
                saq.clearDiscarded(discardedId);
            }
        }
        List<SemanticNetworkEntryPoint> tmp = sls.stream().max(Comparator.comparingDouble(x -> new HashSet<>(x).size() * map.get(x))).orElseGet(ArrayList::new);
        if (path != null) path.addAll(tmp);

        return countDisambiguations;
    }

    @Override
    public void close() {
        try {
            cia.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void forceTrue(SemanticNetworkEntryPoint key, SemanticNetworkEntryPoint y) {
        saq.force(key, y);
    }

    public void pushQueryStart(SemanticNetworkEntryPoint root, SemanticNetworkEntryPoint ev) {
        saq.pushQueryStart(root, ev);
    }

    /*public FuzzyMatcher<ConceptNet5Postgres.RecordResultForSingleNode> getEnrichedVocabulary() {
        return enrichedVocabulary;
    }*/

    @Override
    public String getDimension() {
        return dimension;
    }

    public void pushQueryStart(EdgeVertex ev) {
        saq.pushQueryStart(ev);
    }

    /**
     * Class used to evaluate the disambiguation at the next recursive step
     */
    private class CountDisambiguations {
        private SemanticNetworkEntryPoint hierarchyRoot;
        private boolean alreadyContains;
        private double countDisambiguations;
        private double countOk;
        private SemanticNetworkEntryPoint term;
        private double u;
        private boolean doCountOk;

        /**
         * @param type                 Root type to check
         * @param alreadyContains      If the element is already contained (TODO: in which cases?)
         * @param countDisambiguations DisambiguatedValue number at the current step
         * @param countOk              Current state of ok elements
         * @param newTerm              Next term to be traversed
         * @param u                    Uncertainty score associated to the traversed relation (whether it is approximated or not)
         * @param doCountOk            Whether I have to increment the CountOk number
         */
        public CountDisambiguations(SemanticNetworkEntryPoint type, boolean alreadyContains, double countDisambiguations, double countOk, SemanticNetworkEntryPoint newTerm, double u, boolean doCountOk) {
            this.hierarchyRoot = type;
            this.alreadyContains = alreadyContains;
            this.countDisambiguations = countDisambiguations;
            this.countOk = countOk;
            this.term = newTerm;
            this.u = u;
            this.doCountOk = doCountOk;
        }

        public double getCountDisambiguations() {
            return countDisambiguations;
        }

        public double getCountOk() {
            return countOk;
        }

        public CountDisambiguations invoke(List<SemanticNetworkEntryPoint> terminology) {
            double d;
            if (term.isStopPointFor(hierarchyRoot) /*|| (alreadyContains && saq.vertexExists(term))*/) {
                d = 1.0;
                /*if (alreadyContains) {
                    cia.completeExisting(term, hierarchyRoot, terminology);
                    terminology.add(0, term);
                }*/
            } else {
                cia.resetVisitedTerms();
                if (enrichedVocabulary.isStopWord(term.getValue()))
                    d = 0.0;
                else
                    d = cia.checkIsA(term, hierarchyRoot, limit > 6 ? 4 : limit, alreadyContains, terminology, new Stack<>()) * u;
                cia.resetVisitedTerms();
            }
            d = Math.abs(d);
            if (d > 1) d = 1.0;             // Any score greater than one is mapped into 1
            if (Double.isNaN(d)) d = 0;     // Any score that failed to evaluate is mapped to zero
            if (doCountOk && (d == 1.0)) countOk++; // Counts eventually how many elements were ok
            countDisambiguations *= (1 - d);
            return this;
        }
    }
}
