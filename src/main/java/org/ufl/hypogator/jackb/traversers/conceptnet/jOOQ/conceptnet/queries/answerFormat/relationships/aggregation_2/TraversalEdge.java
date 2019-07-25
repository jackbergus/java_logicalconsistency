/*
 * TraversalEdge.java
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

package org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.relationships.aggregation_2;

import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.relationships.GenericEdge;
import org.ufl.hypogator.jackb.scraper.SemanticNetworkEntryPoint;

import java.util.List;

public class TraversalEdge extends GenericEdge<CoarsenedHierarchicalType> {

    public TraversalEdge(SemanticNetworkEntryPoint src, SemanticNetworkEntryPoint dst, String surfaceText, double weight, List<String> context, CoarsenedHierarchicalType coarsenedHierarchicalType) {
        super(src, dst, surfaceText, weight, context, coarsenedHierarchicalType);
    }

    /*public String srcLabel() {
        String arr[] = src.term.split("/");
        return (arr == null || arr.length == 0) ? src.label : arr[arr.length - 1];
    }

    public String dstLabel() {
        String arr[] = dst.term.split("/");
        return (arr == null || arr.length == 0) ? dst.label : arr[arr.length - 1];
    }*/

    public TraversalEdge flip() {
        return new TraversalEdge(dst, src, surfaceText, uncertainty(), context, getType());
    }
}
