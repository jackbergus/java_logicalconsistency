/*
 * SemanticEdge.java
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

package org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.relationships.aggregation_1;


import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.Edge;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.EdgeVertex;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.relationships.GenericEdge;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.relationships.aggregation_2.CoarsenedHierarchicalType;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.relationships.aggregation_2.TraversalEdge;
import org.ufl.hypogator.jackb.scraper.SemanticNetworkEntryPoint;

import java.util.List;

public class SemanticEdge extends GenericEdge<HierarchicalTraverseEdgeType> {

    private SemanticEdge(SemanticNetworkEntryPoint src, SemanticNetworkEntryPoint dst, String surfaceText, double weight, List<String> context, HierarchicalTraverseEdgeType relationship) {
        super(src, dst, surfaceText, weight, context, relationship);
    }

    private SemanticEdge(Edge e) {
        this(e.getSource(), e.getTarget(), e.getSurfaceText(), e.uncertainty(), e.getContext(), HierarchicalTraverse.createSemanticEdgeType(e.getRelationship()));
    }

    public static SemanticEdge fromEdge(Edge e) {
        return new SemanticEdge(e).normalized();
    }

    public static SemanticEdge fromArguments(EdgeVertex src, EdgeVertex dst, String surfaceText, double weight, List<String> context, HierarchicalTraverseEdgeType relationship) {
        return new SemanticEdge(src, dst, surfaceText, weight, context, relationship).normalized();
    }

    public SemanticEdge normalized() {
        return type.isPreferredDirection() ? this : symmetry();
    }

    /**
     * Inverts the source and target relation. Contemporairly, it reverts the
     *
     * @return
     */
    public SemanticEdge symmetry() {
        return new SemanticEdge(dst, src, surfaceText, uncertainty(), context, type.invertRelationType());
    }

    /**
     * Negates the representation of the relation
     *
     * @return
     */
    public SemanticEdge negate() {
        return new SemanticEdge(src, dst, surfaceText, uncertainty(), context, type.negate());
    }

    /**
     * Provides a coarser representation of the finer ones for reasoning purposes.
     * general --> specific
     *
     * @return
     */
    public TraversalEdge coarser() {
        CoarsenedHierarchicalType t;
        switch (getType().type) {
            case GenericConceptRelatedness:
                t = CoarsenedHierarchicalType.Similar;
                break;

            case RightToLeftImplication:
                return symmetry().coarser();

            case HasContentInstance:
            case PartOfIsA:
            case LeftToRightImplication:
                t = CoarsenedHierarchicalType.CausedBy;
                break;

            case Equality:
                t = CoarsenedHierarchicalType.Equals;
                break;

            default:
                t = CoarsenedHierarchicalType.None;
                break;
        }


        /**
         * Returns a relation representing the preferred representation for traversing Concepts for element extraction
         */
        return new TraversalEdge(src, dst, surfaceText, uncertainty() == 0 ? 1 : uncertainty(), context, t);
    }


    public static CoarsenedHierarchicalType coarser(boolean forTraversingSearch, HierarchicalTraverse type) {
        CoarsenedHierarchicalType t;
        switch (type) {
            case GenericConceptRelatedness:
                return CoarsenedHierarchicalType.Similar;


            case HasContentInstance:
                return CoarsenedHierarchicalType.CausedBy;

            case RightToLeftImplication:
                return !forTraversingSearch ? CoarsenedHierarchicalType.CausedBy : CoarsenedHierarchicalType.None;

            case PartOfIsA:
            case LeftToRightImplication:
                return coarser(forTraversingSearch, type.invertRelationDirection());

            case Equality:
                return CoarsenedHierarchicalType.Equals;

            default:
                return CoarsenedHierarchicalType.None;
        }
    }

    public boolean isNegated() {
        return !getType().affermative;
    }

    /*public String srcLabel() {
        String arr[] = src.term.split("/");
        return (arr == null || arr.length == 0) ? src.label : arr[arr.length - 1];
    }

    public String dstLabel() {
        String arr[] = dst.term.split("/");
        return (arr == null || arr.length == 0) ? dst.label : arr[arr.length - 1];
    }*/
}
