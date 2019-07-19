/*
 * ConceptNetTraverser.java
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

package org.ufl.hypogator.jackb.traversers.conceptnet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.ConceptNetVocabulary;
import org.ufl.hypogator.jackb.fuzzymatching.FuzzyMatcher;
import org.ufl.hypogator.jackb.m9.configuration.Concept5ClientConfigurations;
import org.ufl.hypogator.jackb.scraper.SemanticNetworkEntryPoint;
import org.ufl.hypogator.jackb.scraper.adt.SemanticNetworkTraversers;
import org.ufl.hypogator.jackb.streamutils.data.ArraySupport;
import org.ufl.hypogator.jackb.streamutils.data.DataIterator;
import org.ufl.hypogator.jackb.streamutils.iterators.IteratorWithOperations;
import org.ufl.hypogator.jackb.streamutils.iterators.VoidIterator;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.Edge;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.EdgeVertex;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.db.PgObjectString;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.relationships.aggregation_1.SemanticEdge;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.relationships.aggregation_2.CoarsenedHierarchicalType;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.relationships.raw_type.RelationshipTypes;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Function;

public class ConceptNetJNITraverser implements SemanticNetworkTraversers<RelationshipTypes> {

    public final static ConceptNet5Dump dumpingGround = ConceptNet5Dump.getInstance();
    private final static Concept5ClientConfigurations conf = Concept5ClientConfigurations.instantiate();

    private static ConceptNetJNITraverser self = null;
    public static ConceptNetJNITraverser getInstance() {
        if (self == null)
            self = new ConceptNetJNITraverser();
        return self;
    }

    private static final JNIEntryPoint ep = JNIEntryPoint.getInstance();
    private final boolean onlyEnglishConcept;
    private FuzzyMatcher<RecordResultForSingleNode>  voc;

    public static final RelationshipTypes[] rel = new RelationshipTypes[]
            { RelationshipTypes.RelatedTo };

    public static final RelationshipTypes[] sta = new RelationshipTypes[]
            { RelationshipTypes.IsA, RelationshipTypes.InstanceOf, RelationshipTypes.genre, RelationshipTypes.genus };


    public static final RelationshipTypes[] std = new RelationshipTypes[]
            { RelationshipTypes.IsA, RelationshipTypes.InstanceOf, RelationshipTypes.genre, RelationshipTypes.genus,
                    RelationshipTypes.PartOf };

    private ConceptNetJNITraverser() {
        this(null);
    }

    private ConceptNetJNITraverser(ConceptNetVocabulary voc) {
        this.onlyEnglishConcept = conf.retrieveOnlyEnglishConcepts();
        this.voc = voc;
        if (ep != null)
            ep.init();
    }

    @Override
    public Iterable<Edge> synonymsOutgoing(SemanticNetworkEntryPoint currentHierarchyElement) {
        throw new NotImplementedException();
        //return conf.outgoingRelDefaultEn(currentHierarchyElement.asConceptNetId(), synonymType().toString());
    }

    @Override
    public Iterable<SemanticEdge> similarIngoing(SemanticNetworkEntryPoint currentHierarchyElement) {
        return streamForSimilarValues((y) -> this.ingoingRelDefaultEn(currentHierarchyElement.asConceptNetId(), y));
    }

    @Override
    public RelationshipTypes[] relatedTypes() {
        return rel;
    }

    @Override
    public RelationshipTypes[] superAscendingTypes() {
        return sta;
    }

    @Override
    public RelationshipTypes[] superDescendingTypes() {
        return std;
    }

    @Override
    public RelationshipTypes synonymType() {
        return RelationshipTypes.Synonym;
    }

    @Override
    public Iterable<SemanticEdge> getRelatedEdges(SemanticNetworkEntryPoint term, boolean isOutgoing) {
        String id = term.asConceptNetId();
        return streamForValues((y) -> isOutgoing ? this.outgoingRelDefaultEn(id == null ? term.getSemanticId() : id, y) : this.ingoingRelDefaultEn(id == null ? term.getSemanticId() : id, y), relatedTypes());
    }

    @Override
    public Iterable<SemanticEdge> getSemanticUpwardEdges(SemanticNetworkEntryPoint currentHierarchyElement) {
        String id = currentHierarchyElement.asConceptNetId();
        return streamForValues((y) -> this.outgoingRelDefaultEn(id == null ? currentHierarchyElement.getSemanticId() : id, y), superAscendingTypes());
    }

    @Override
    public SemanticNetworkEntryPoint resolveTerm(String term) {
        return resolveEntryPoint(term, voc);
        /// TODO: throw new UnsupportedOperationException("Error: I have to put also the graph loaded in here");
        // TODO: also return conf.resolveEntryPoint(term), that I may miss
    }

    public EdgeVertex resolveEntryPoint(String term, FuzzyMatcher<RecordResultForSingleNode> vocabulary) {
        if (term.equals("Union") || conf.conceptnetResolvableTypes().contains(term))
            return EdgeVertex.generateSemanticRoot(term);
        else {
            // Checking whether the vocabulary has some graph term
            Collection<RecordResultForSingleNode> singleton = null;
            if (vocabulary != null)
                singleton = vocabulary.containsExactTerm2(term);
            EdgeVertex toReturn = null;
            // Extracting the graph-based element
            if (singleton != null && !singleton.isEmpty()) {
                toReturn = singleton.iterator().next().getParent();
            }
            return toReturn == null ? dumpingGround.queryNode(false, term) : toReturn;
        }
    }

    @Override
    public Iterable<SemanticEdge> descendHierarchy(SemanticNetworkEntryPoint currentHierarchyElement) {
        return streamForValues((y) -> this.ingoingRelDefaultEn(currentHierarchyElement.asConceptNetId(), y), superDescendingTypes());
    }

    /**
     * @param f       Function defining the way to traverse Concepts
     * @param t       Types that we want to select
     * @return Filtered edges
     */
    private Iterable<SemanticEdge> streamForValues(Function<Long, IteratorWithOperations<Edge>> f, RelationshipTypes... t) {
        Long map = Arrays.stream(t).map(x -> x.value).reduce(0L, (x, y) -> x | y);
        return () -> f
                .apply(map)
                .filter(localEdge -> ((!onlyEnglishConcept) || localEdge.isAnglophone()))
                .map(SemanticEdge::fromEdge);
    }

    private Iterable<SemanticEdge> streamForSimilarValues(Function<Long, IteratorWithOperations<Edge>> f) {
        Long map = Arrays.stream(RelationshipTypes.values()).filter(x -> x.coarser().equals(CoarsenedHierarchicalType.Similar) && x.isSuitableForHierarchyTraversing()).map(x -> x.value).reduce(0L, (x, y) -> x | y);
        return () -> f
                .apply(map)
                .filter(localEdge -> ((!onlyEnglishConcept) || localEdge.isAnglophone()))
                .map(SemanticEdge::fromEdge);
    }

    private static ObjectMapper mapper = Concept5ClientConfigurations.instantiate().jsonSerializer;
    private static IteratorWithOperations<Edge> emptyDataIterator = new DataIterator<Edge>(Collections.emptyIterator());

    private IteratorWithOperations<Edge> ingoingRelDefaultEn(String term, Long relMap) {
        if (ep == null) return emptyDataIterator;

        Long res1 = dumpingGround.nodeIdToOffset(term);
        if (res1 == null)
            return VoidIterator.getInstance();
        return new DataIterator<>(new ArraySupport<>(ep.pgObjectOut(res1, true, relMap)))
                .map(x -> {try {
                                return mapper.readValue(x, PgObjectString.class);
                            } catch (IOException e) {
                                return null;
                            }})
                .filter(Objects::nonNull)
                .map(PgObjectString::asBackwardCompatibilityEdge);
    }

    private IteratorWithOperations<Edge> outgoingRelDefaultEn(String term, Long relMap) {
        if (ep == null) return emptyDataIterator;

        Long res1 = dumpingGround.nodeIdToOffset(term);
        if (res1 == null)
            return VoidIterator.getInstance();
        return new DataIterator<>(new ArraySupport<>(ep.pgObjectOut(res1, false, relMap)))
                .map(x -> {try {
                    return mapper.readValue(x, PgObjectString.class);
                } catch (IOException e) {
                    return null;
                }})
                .filter(Objects::nonNull)
                .map(PgObjectString::asBackwardCompatibilityEdge);
    }

    public void setVocabulary(FuzzyMatcher<RecordResultForSingleNode> enrichedVocabulary) {
        this.voc = enrichedVocabulary;
    }

}
