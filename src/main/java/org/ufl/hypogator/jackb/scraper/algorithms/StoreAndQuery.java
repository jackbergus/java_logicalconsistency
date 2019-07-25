/*
 * StoreAndQuery.java
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

package org.ufl.hypogator.jackb.scraper.algorithms;

import org.ufl.hypogator.jackb.logger.Logger;
import org.ufl.hypogator.jackb.logger.LoggerFactory;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.relationships.aggregation_2.CoarsenedHierarchicalType;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.relationships.aggregation_2.TraversalEdge;
import org.ufl.hypogator.jackb.scraper.adt.DiGraph;
import org.ufl.hypogator.jackb.scraper.adt.QuadrupleScraper;
import org.ufl.hypogator.jackb.scraper.SemanticNetworkEntryPoint;

import java.util.*;

public class StoreAndQuery {
    private final DiGraph<SemanticNetworkEntryPoint> toUpper;
    private final DiGraph<String> equivalenceClass;
    private final SemanticNetworkEntryPoint types;
    private final Deque<QuadrupleScraper> cacheQuery;
    private static final Logger log = LoggerFactory.getLogger(StoreAndQuery.class);

    public StoreAndQuery(DiGraph<SemanticNetworkEntryPoint> toUpper, DiGraph<String> eqClass, SemanticNetworkEntryPoint equivalentRootTypes) {
        this.toUpper = toUpper;
        this.equivalenceClass = eqClass;
        types = equivalentRootTypes;
        cacheQuery = new ArrayDeque<>();
    }

    public double storeAndQueryEquivalenceClass(SemanticNetworkEntryPoint hierarchyRoot, SemanticNetworkEntryPoint eq, SemanticNetworkEntryPoint currentHierarchyElement, double penalty, int count, TraversalEdge edge) {
        double d = -1;
        if (!edge.type.equals(CoarsenedHierarchicalType.None)) { // if this is a "descending" property of the object
            if ((d = isValidTraversalEdge(edge, currentHierarchyElement, penalty, true)) >= 0) {
                log.debug(edge.getSrc().getValue()+" coming next...");
                cacheQuery.push(new QuadrupleScraper(hierarchyRoot, edge.getSrc(), count, d));
                // adding the equivalence class elements that are missing from the main graph
                equivalenceClass.add(eq.getSemanticId(), currentHierarchyElement.getSemanticId(), d);
                equivalenceClass.add(currentHierarchyElement.getSemanticId(), eq.getSemanticId(), d);
            }
        }
        return d;
    }

    /**
     * This method is called when I know for sure that the provided edge is a good edge for my hierarchy
     * @param hierarchyRoot             Main root to which all the element belong
     * @param currentHierarchyElement
     * @param penalty
     * @param count
     * @param edge
     * @return
     */
    public double storeAndQuery(SemanticNetworkEntryPoint hierarchyRoot, SemanticNetworkEntryPoint currentHierarchyElement, double penalty, int count, TraversalEdge edge) {
        double d = -1;
        if (!edge.type.equals(CoarsenedHierarchicalType.None)) { // if this is a "descending" property of the object
            if ((d = isValidTraversalEdge(edge, currentHierarchyElement, penalty, false)) >= 0) {
                cacheQuery.push(new QuadrupleScraper(hierarchyRoot, edge.getSrc(), count, d));
            }
        }
        return d;
    }

    public void force(SemanticNetworkEntryPoint src, SemanticNetworkEntryPoint dst) {
        toUpper.add(dst, src, 1.0);
    }

    /**
     *
     * @param e
     * @param currentHierarchyElement
     * @param penalty
     * @param isEquivalenceClass            Equivalence classes are not reported in the final graph
     * @return
     */
    public double isValidTraversalEdge(TraversalEdge e, SemanticNetworkEntryPoint currentHierarchyElement, double penalty, boolean isEquivalenceClass) {
        double u = Math.sqrt(Math.abs(e.uncertainty() * penalty));
        if (
                ((!e.dst.hasPOS()) || e.dst.getPOS().equals("n")) &&
                        (types.isStopPointFor(currentHierarchyElement) || u >= 0.25)) {
            // If these conditions are satisfied, then
            if (u == 0.0)
                u = 0.25;
            SemanticNetworkEntryPoint src = e.src;
            SemanticNetworkEntryPoint dst = e.dst;
            if (!src.isStopPointFor(dst)) {
                if ((!isEquivalenceClass) && toUpper.add(src, dst, u)) {
                    return u;
                } else {
                    return -1; // not continue to visit from here
                }
            } else return -1;
        } else
            return -1;
    }

    public void pushQueryStart(SemanticNetworkEntryPoint root, SemanticNetworkEntryPoint concept) {
        cacheQuery.push(new QuadrupleScraper(root, concept, 0, 1));
    }

    public void pushQueryStart(SemanticNetworkEntryPoint concept) {
        cacheQuery.push(new QuadrupleScraper(concept, concept, 0, 1));
    }

    public boolean hasNoQuery() {
        return cacheQuery.isEmpty();
    }

    public QuadrupleScraper popQuery() {
        return cacheQuery.removeFirst();
    }

    public boolean keyExists(SemanticNetworkEntryPoint label) {
        return toUpper.keyExists(label);
    }

    public void finalize() {

    }

    public void close() {
        // Computes the whole set of equivalence classes
        List<Set<String>> ls = equivalenceClass.connectedComponents();

        // Associating each edge to its id
        HashMap<String, SemanticNetworkEntryPoint> hmm = new HashMap<>();
        for (SemanticNetworkEntryPoint ep : toUpper.vertexSet()) {
            hmm.put(ep.getSemanticId(), ep);
        }

        // Then, associating the candidate to all the elements, including himself
        for (int i = 0, lsSize = ls.size(); i < lsSize; i++) {
            Set<String> clazz = ls.get(i);
            String candidateId = clazz.iterator().next();
            SemanticNetworkEntryPoint candidate = hmm.get(candidateId);
            for (String id : clazz) {
                candidate.addToEquivalenceSet(hmm.get(id));
            }
        }

        // Now, each vertex is
    }

    public void clearDiscarded(Set<String> discarded) {
        ArrayList<QuadrupleScraper> al = new ArrayList<>();
        for (QuadrupleScraper x : this.cacheQuery) {
            if (discarded.contains(x.current))
                al.add(x);
        }
        cacheQuery.removeAll(al);
    }
}
