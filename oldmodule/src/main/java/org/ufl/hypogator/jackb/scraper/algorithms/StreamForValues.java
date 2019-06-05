/*
 * StreamForValues.java
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

import org.ufl.hypogator.jackb.traversers.conceptnet.Concept5ClientConfigurations;
import org.ufl.hypogator.jackb.traversers.conceptnet.TraverseConceptNet;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.relationships.aggregation_1.SemanticEdge;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.relationships.aggregation_2.CoarsenedHierarchicalType;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.relationships.raw_type.RelationshipTypes;
import java.util.Arrays;
import java.util.stream.StreamSupport;

public class StreamForValues {

    final Concept5ClientConfigurations conf;
    private final boolean onlyEnglishConcept;

    public StreamForValues(Concept5ClientConfigurations conf) {
        this.conf = conf;
        this.onlyEnglishConcept = conf.retrieveOnlyEnglishConcepts();
    }

    /**
     * @param f       Function defining the way to traverse Concepts
     * @param negated If we want to return negated edges, set this variable to true
     * @param t       Types that we want to select
     * @return Filtered edges
     */
    public Iterable<SemanticEdge> streamForValues(TraverseConceptNet f, boolean negated, RelationshipTypes... t) {
        return () -> Arrays.stream(t)
                .flatMap(x -> StreamSupport.stream(f.apply(conf, x.toString()).spliterator(), true))
                .filter(localEdge -> ((!onlyEnglishConcept) || localEdge.isAnglophone()))
                .map(SemanticEdge::fromEdge)
                .filter(x -> !negated || !x.isNegated())
                .iterator();
    }

    public Iterable<SemanticEdge> streamForValues(CoarsenedHierarchicalType broader, TraverseConceptNet f, boolean negated) {
        return () -> Arrays.stream(RelationshipTypes.values())
                .filter(x -> x.coarser().equals(broader) && x.isSuitableForHierarchyTraversing())
                .flatMap(x -> StreamSupport.stream(f.apply(conf, x.toString()).spliterator(), true))
                .filter(localEdge -> ((!onlyEnglishConcept) || localEdge.isAnglophone()))
                .map(SemanticEdge::fromEdge)
                .filter(x -> !negated || !x.isNegated())
                .iterator();
    }

}
