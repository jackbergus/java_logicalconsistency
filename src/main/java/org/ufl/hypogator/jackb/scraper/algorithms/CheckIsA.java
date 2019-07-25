/*
 * CheckIsA.java
 * This file is part of aida_scraper
 *
 * Copyright (C) 2018 giacomo
 *
 * aida_scraper is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * aida_scraper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aida_scraper. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.ufl.hypogator.jackb.scraper.algorithms;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.ConceptNetVocabulary;
import org.ufl.hypogator.jackb.fuzzymatching.FuzzyMatcher;
import org.ufl.hypogator.jackb.fuzzymatching.TwoGramIndexerJNI;
import org.ufl.hypogator.jackb.logger.Logger;
import org.ufl.hypogator.jackb.logger.LoggerFactory;
import org.ufl.hypogator.jackb.scraper.adt.DiGraphEquivalenceClass;
import org.ufl.hypogator.jackb.traversers.conceptnet.ConceptNet5Postgres;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.EdgeVertex;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.relationships.aggregation_1.SemanticEdge;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.relationships.aggregation_2.TraversalEdge;
import org.ufl.hypogator.jackb.scraper.adt.DiGraph;
import org.ufl.hypogator.jackb.scraper.adt.SemanticNetworkTraversers;
import org.ufl.hypogator.jackb.scraper.SemanticNetworkEntryPoint;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

/**
 * This is the first class that is directly invoked/used for graph traversals
 * @param <DataSpecificRelationships>
 */
public class CheckIsA<DataSpecificRelationships> {

    private final File file;
    private final String dimension;
    private DiGraphEquivalenceClass toUpper;
    private DiGraph<String> eqClass;
    private Set<String> visitedTerms;
    private final FuzzyMatcher<ConceptNet5Postgres.RecordResultForSingleNode> enrichedVocabulary;
    private final SemanticNetworkTraversers<DataSpecificRelationships> pg_dump_traverser;
    private final static ObjectMapper om = new ObjectMapper();
    private final static Logger logger = LoggerFactory.getLogger(CheckIsA.class);

    public FuzzyMatcher<ConceptNet5Postgres.RecordResultForSingleNode> getEnrichedVocabularyWithHierarchy() {
        return enrichedVocabulary;
    }

    private   DiGraphEquivalenceClass readFromFile(File qdmapFolder, Function<String, SemanticNetworkEntryPoint> tf) {
        if (qdmapFolder.exists()) {
            try {
                //System.err.println("VocSize for "+ dimension +" : "+enrichedVocabulary.getSize());
                if (enrichedVocabulary instanceof ConceptNetVocabulary)
                    return new DiGraphEquivalenceClass().loadFromFile(qdmapFolder, ((ConceptNetVocabulary)enrichedVocabulary), tf);
                else
                    return new DiGraphEquivalenceClass().loadFromFile(qdmapFolder, null, tf);
            } catch (IOException e) {
                return new DiGraphEquivalenceClass();
            }
        } else {
            return new DiGraphEquivalenceClass();
        }
    }

    public CheckIsA(SemanticNetworkTraversers<DataSpecificRelationships> pg_dump_traverser, File graphFile, String dimension) {
        visitedTerms = new HashSet<>();
        this.pg_dump_traverser = pg_dump_traverser;
        this.dimension = dimension;

        // Loads the vocabulary that should be memorized inside the seed maps/hashmaps. In this way, the content is not duplicated.
        // The content not to be duplicated contains the whols
        enrichedVocabulary = ConceptNetVocabulary.readDefaultVocabulary().copy();

        // This method also enriches the voc vocabulary
        toUpper = readFromFile(graphFile, x -> {
            try {
                return om.readValue(x, EdgeVertex.class);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });

        this.file = graphFile;
    }

    public CheckIsA(SemanticNetworkTraversers<DataSpecificRelationships> pg_dump_traverser, TwoGramIndexerJNI.TwoGramIndexerForDimension graphFile, String dimension) {
        visitedTerms = new HashSet<>();
        this.pg_dump_traverser = pg_dump_traverser;
        this.dimension = dimension;

        // Loads the vocabulary that should be memorized inside the seed maps/hashmaps. In this way, the content is not duplicated.
        // The content not to be duplicated contains the whols
        enrichedVocabulary = graphFile.getEnrichedVocabulary();

        // This method also enriches the voc vocabulary
        toUpper = graphFile.getGraph();
        this.file = graphFile.getHierarchyFile();
    }

    public void resetVisitedTerms() {
        visitedTerms.clear();
    }

    /*public double checkIsA(SemanticNetworkEntryPoint term, SemanticNetworkEntryPoint root, int boundLimit) {
        return checkIsA(term, root, boundLimit, false, null);
    }*/

    public boolean containsVertex(SemanticNetworkEntryPoint term) {
        return toUpper.vertexExists(term);
    }

    /**
     * Uses the stored hierarchy to complete the path information.
     *
     * @param term
     * @param root
     * @param path
     * @return
     */
    private long size = 0;
    public boolean completeExisting(SemanticNetworkEntryPoint term, SemanticNetworkEntryPoint root, List<SemanticNetworkEntryPoint> path) {
        toUpper.invertPathDistance();
        if (path != null) {
            List<DiGraph<SemanticNetworkEntryPoint>.Vertex> ls = toUpper.getPath(term, root);
            // If either I have no term no root, I return null
            // If there exist the vertex but no path exists, then
            //   the path has length zero (that means, I have only one vertex)
            if (ls != null && ls.size() > 1) {
                ls.stream().map(x -> x.value).forEach(path::add);
                if (size != toUpper.getGraphSize()) {
                    size = toUpper.getGraphSize();
                    //System.err.println(dimension + " " + size);
                }
                toUpper.revertPathDistance();
                return true;
            }
        }
        if (size != toUpper.getGraphSize()) {
            size = toUpper.getGraphSize();
            //System.err.println(dimension + " " + size);
        }
        toUpper.revertPathDistance();
        return false;
    }

    public double checkIsA(SemanticNetworkEntryPoint term, SemanticNetworkEntryPoint root, int boundLimit, boolean alreadyContains, List<SemanticNetworkEntryPoint> path, Stack<SemanticNetworkEntryPoint> sp) {
        if (enrichedVocabulary.isStopWord(term.getValue())) {
            return 0.0;
        }
        String queried = root.ensureValue() + " <--?-- " + term.ensureValue();
        double ret = 0.0;
        if (term.isStopPointFor(root) || (alreadyContains && toUpper.vertexExists(term)) || term.getValue().toLowerCase().equals(root.getValue().toLowerCase())) {
            logger.debug(queried+" = "+1.0);
            if (path != null) path.add(term);
            return 1.0;
        } else if (boundLimit <= 0 || visitedTerms.contains(term.getSemanticId())) {
            if (sp.contains(term)) {
                logger.debug(queried+" = "+0.9);
                return .9;
                //If the term has been already traversed, then he undergoes self validation
            } else {
                logger.debug(queried+" = "+0);
                return 0.0;
            }
        } else {
            if (!visitedTerms.add(term.getSemanticId())) return Double.NaN;

            double dVal = 0.0;
            double dCount = 0.0;

            // Traversing the instances                                                                                                                      RelationshipTypes.InstanceOf, RelationshipTypes.IsA, RelationshipTypes.genre, RelationshipTypes.genus
            for (SemanticEdge semantic : pg_dump_traverser.getSemanticUpwardEdges(term)/*stream.streamForValues((x, y) -> x.outgoingRelDefaultEn(term, y), false, RelationshipTypes.InstanceOf, RelationshipTypes.IsA, RelationshipTypes.genre, RelationshipTypes.genus)*/) {
                TraversalEdge edge = semantic.coarser();
                if ((alreadyContains && toUpper.vertexExists(edge.dst)) || edge.dst.isStopPointFor(root)) {
                    logger.debug(queried+" = "+1.0);
                    if (path != null) {
                        path.add(term);
                        if (alreadyContains) {
                            path.add(edge.dst);
                            // TODO: completeExisting(edge.dstLabel(), root, path);
                        } else
                            path.add(root);

                    }
                    return 1.0;
                } else {
                    SemanticNetworkEntryPoint edgeDstLabel = edge.dst;
                    sp.add(term);
                    double cia = checkIsA(edgeDstLabel, root, boundLimit - 1, alreadyContains, path, sp);
                    sp.pop();
                    if (!Double.isNaN(cia)) {
                        dCount++;
                        //double new_ = Math.abs(edge.uncertainty());
                        if (cia == 1.0) {
                            if (path != null) path.add(edgeDstLabel);
                            return Math.sqrt(edge.uncertainty() * cia);
                        } else if (cia > 0) {
                            dVal += Math.sqrt(edge.uncertainty() * cia);
                        }
                    }

                }
            }

            // I have to traverse the related-to tags if and only if I have no instanceOf edges.
            // The reason to do so is to reduce the amount of possible errors
            if (dCount == 0) {
                for (SemanticEdge semantic : /*stream.streamForValues((x, y) -> x.outgoingRelDefaultEn(term, y), false, RelationshipTypes.RelatedTo)*/pg_dump_traverser.getRelatedEdges(term, true)) {
                    TraversalEdge edge = semantic.coarser();
                    if ((alreadyContains && toUpper.vertexExists(edge.dst)) || edge.getDst().isStopPointFor(root)) {
                        logger.debug(queried+" = "+.95);
                        if (path != null) {
                            path.add(term);
                            if (alreadyContains) {
                                path.add(edge.dst);
                                completeExisting(edge.dst, root, path);
                            } else
                                path.add(root);
                        }
                        return .95;
                    } else {
                        SemanticNetworkEntryPoint edgeDstLabel = edge.dst;
                        sp.push(term);
                        double cia = checkIsA(edgeDstLabel, root, boundLimit - 1, alreadyContains, path, sp);
                        sp.pop();
                        if (!Double.isNaN(cia)) {
                            dCount++;
                            //double new_ = Math.abs(edge.uncertainty())*.9;
                            if (cia == 1.0) {
                                if (path != null) path.add(edgeDstLabel);
                                return Math.sqrt(edge.uncertainty() * cia);
                            } else if (cia > 0) {
                                dVal += Math.sqrt(edge.uncertainty() * cia);
                            }
                        }

                    }
                }
                for (SemanticEdge semantic : /*stream.streamForValues((x, y) -> x.ingoingRelDefaultEn(term, y), false, RelationshipTypes.RelatedTo)*/ pg_dump_traverser.getRelatedEdges(term, false)) {
                    TraversalEdge edge = semantic.coarser();
                    if ((alreadyContains && toUpper.vertexExists(edge.src)) || edge.src.isStopPointFor(root)) {
                        logger.debug(queried+" = "+.95);
                        if (path != null) {
                            path.add(term);
                            if (alreadyContains) {
                                path.add(edge.dst);
                                completeExisting(edge.dst, root, path);
                            } else
                                path.add(root);
                        }
                        return .95;
                    } else {
                        SemanticNetworkEntryPoint edgeDstLabel = edge.src;
                        sp.push(term);
                        double cia = checkIsA(edgeDstLabel, root, boundLimit - 1, alreadyContains, path, sp);
                        sp.pop();
                        if (!Double.isNaN(cia)) {
                            dCount++;
                            //double new_ = Math.abs(edge.uncertainty())*.9;
                            if (cia == 1.0) {
                                if (path != null) path.add(edgeDstLabel);
                                return Math.sqrt(edge.uncertainty() * cia);
                            } else if (cia > 0) {
                                dVal += Math.sqrt(edge.uncertainty() * cia);
                            }
                        }

                    }
                }
            }

            if (dCount != 0.0) {
                ret = dVal / dCount;
            } else {
                ret = 0;
            }
        }
        logger.debug("final outcome "+ queried+" = "+ret);
        return ret;
    }

    public StoreAndQuery genereateStoreAndQuery(SemanticNetworkEntryPoint equivalentTypes) {
        if (eqClass == null) {
            eqClass = new DiGraph<>();
            try {
                eqClass = eqClass.loadFromFile(new File(file.getAbsolutePath()+"_eqClass.graph"), null, String::toString);
            } catch (IOException e) {
            }
        }
        return new StoreAndQuery(toUpper, eqClass, equivalentTypes);
    }

    public void close() throws IOException {
        // At this point, the grpah after the visit should contain all the elements witin the equivalence class
        toUpper.writeToFile(file, Object::toString);
    }
}
