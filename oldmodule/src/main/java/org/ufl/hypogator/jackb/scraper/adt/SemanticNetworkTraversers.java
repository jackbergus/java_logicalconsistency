/*
 * SemanticNetworkTraversers.java
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

package org.ufl.hypogator.jackb.scraper.adt;

import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.Edge;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.relationships.aggregation_1.SemanticEdge;
import org.ufl.hypogator.jackb.scraper.SemanticNetworkEntryPoint;

public interface SemanticNetworkTraversers<DataSpecificRelationships> {
    Iterable<Edge> synonymsOutgoing(SemanticNetworkEntryPoint currentHierarchyElement);
    Iterable<SemanticEdge> similarIngoing(SemanticNetworkEntryPoint currentHierarchyElement);
    Iterable<SemanticEdge> descendHierarchy(SemanticNetworkEntryPoint current_element);
    Iterable<SemanticEdge> getRelatedEdges(SemanticNetworkEntryPoint currentHierarchyElement, boolean isOutgoing);
    Iterable<SemanticEdge> getSemanticUpwardEdges(SemanticNetworkEntryPoint currentHierarchyElement);
    SemanticNetworkEntryPoint resolveTerm(String term);

    DataSpecificRelationships[] relatedTypes();
    DataSpecificRelationships[] superAscendingTypes();
    DataSpecificRelationships[] superDescendingTypes();
    DataSpecificRelationships synonymType();

}
