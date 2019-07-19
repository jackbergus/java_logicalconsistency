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

import com.google.common.collect.Streams;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.ConceptNetVocabulary;
import org.ufl.hypogator.jackb.m9.configuration.Concept5ClientConfigurations;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.Edge;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.relationships.aggregation_1.SemanticEdge;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.relationships.aggregation_2.CoarsenedHierarchicalType;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.relationships.raw_type.RelationshipTypes;
import org.ufl.hypogator.jackb.scraper.adt.SemanticNetworkTraversers;
import org.ufl.hypogator.jackb.scraper.SemanticNetworkEntryPoint;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class sConceptNetTraverser implements SemanticNetworkTraversers<RelationshipTypes> {

    private final Concept5ClientConfigurations conf;
    private final boolean onlyEnglishConcept;
    private ConceptNetVocabulary voc;

    public static final RelationshipTypes[] rel = new RelationshipTypes[]
            { RelationshipTypes.RelatedTo };

    public static final RelationshipTypes[] sta = new RelationshipTypes[]
            { RelationshipTypes.IsA, RelationshipTypes.InstanceOf, RelationshipTypes.genre, RelationshipTypes.genus };


    public static final RelationshipTypes[] std = new RelationshipTypes[]
            { RelationshipTypes.IsA, RelationshipTypes.InstanceOf, RelationshipTypes.genre, RelationshipTypes.genus,
                    RelationshipTypes.PartOf };

    public ConceptNetTraverser() {
        this(Concept5ClientConfigurations.instantiate(), null);
    }

    public ConceptNetTraverser(Concept5ClientConfigurations conf, ConceptNetVocabulary voc) {
        this(conf, conf.retrieveOnlyEnglishConcepts(), voc);
    }

    public ConceptNetTraverser(Concept5ClientConfigurations conf, boolean onlyEnglishConcept, ConceptNetVocabulary voc) {
        this.conf = conf;
        this.onlyEnglishConcept = onlyEnglishConcept;
        this.voc = voc;
    }

    @Override
    public Iterable<Edge> synonymsOutgoing(SemanticNetworkEntryPoint currentHierarchyElement) {
        return conf.outgoingRelDefaultEn(currentHierarchyElement.asConceptNetId(), synonymType().toString());
    }

    @Override
    public Iterable<SemanticEdge> similarIngoing(SemanticNetworkEntryPoint currentHierarchyElement) {
        return streamForValues(CoarsenedHierarchicalType.Similar, (x, y) -> x.ingoingRelDefaultEn(currentHierarchyElement.asConceptNetId(), y), false);
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
        //stream.streamForValues((x, y) -> x.outgoingRelDefaultEn(term, y), false, RelationshipTypes.RelatedTo)
        return streamForValues((x, y) -> isOutgoing ? x.outgoingRelDefaultEn(id == null ? term.getSemanticId() : id, y) : x.ingoingRelDefaultEn(id == null ? term.getSemanticId() : id, y), false, relatedTypes());
    }

    @Override
    public Iterable<SemanticEdge> getSemanticUpwardEdges(SemanticNetworkEntryPoint currentHierarchyElement) {
        String id = currentHierarchyElement.asConceptNetId();
        return streamForValues((x, y) -> x.outgoingRelDefaultEn(id == null ? currentHierarchyElement.getSemanticId() : id, y), false, superAscendingTypes());
    }

    @Override
    public SemanticNetworkEntryPoint resolveTerm(String term) {
        return conf.resolveEntryPoint(term, voc);
        /// TODO: throw new UnsupportedOperationException("Error: I have to put also the graph loaded in here");
        // TODO: also return conf.resolveEntryPoint(term), that I may miss
    }

    @Override
    public Iterable<SemanticEdge> descendHierarchy(SemanticNetworkEntryPoint currentHierarchyElement) {
        return streamForValues((x, y) -> x.ingoingRelDefaultEn(currentHierarchyElement.asConceptNetId(), y), false,  superDescendingTypes());
    }

    /**
     * @param f       Function defining the way to traverse Concepts
     * @param negated If we want to return negated edges, set this variable to true
     * @param t       Types that we want to select
     * @return Filtered edges
     */
    private Iterable<SemanticEdge> streamForValues(TraverseConceptNet f, boolean negated, RelationshipTypes... t) {
        return //() ->
                /*Arrays.stream(t)
                .flatMap(x -> StreamSupport.stream(f.apply(conf, x.toString()).spliterator(), true))*/
        Streams.stream(f.apply(conf, Arrays.stream(t).map(x -> "/r/"+x.name()).collect(Collectors.joining("|"))))
                .filter(localEdge -> ((!onlyEnglishConcept) || localEdge.isAnglophone()))
                .map(SemanticEdge::fromEdge)
                .filter(x -> !negated || !x.isNegated())
                //.iterator();
                .collect(Collectors.toList());
    }

    private Iterable<SemanticEdge> streamForValues(CoarsenedHierarchicalType broader, TraverseConceptNet f, boolean negated) {
        return //() ->
                StreamSupport.stream(f.apply(conf,
                        Arrays.stream(RelationshipTypes.values())
                                .filter(x -> x.coarser().equals(broader) && x.isSuitableForHierarchyTraversing()).map(x -> "/r/"+x.toString()).collect(Collectors.joining("|"))).spliterator(), true)
                .filter(localEdge -> ((!onlyEnglishConcept) || localEdge.isAnglophone()))
                .map(SemanticEdge::fromEdge)
                .filter(x -> !negated || !x.isNegated())
                //.iterator();
        .collect(Collectors.toList());
    }


    public void setVocabulary(ConceptNetVocabulary enrichedVocabulary) {
        this.voc = enrichedVocabulary;
    }
}
