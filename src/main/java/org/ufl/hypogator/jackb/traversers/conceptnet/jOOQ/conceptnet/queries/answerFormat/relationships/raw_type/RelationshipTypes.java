/*
 * RelationshipTypes.java
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

package org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.relationships.raw_type;

import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.relationships.aggregation_1.HierarchicalTraverse;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.relationships.aggregation_1.HierarchicalTraverseEdgeType;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.relationships.aggregation_1.SemanticEdge;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.relationships.aggregation_2.CoarsenedHierarchicalType;

import java.rmi.UnexpectedException;

public enum RelationshipTypes {
    Antonym(2L),
    AtLocation(4L),
    CapableOf(8L),
    Causes(16L),
    CausesDesire(32L),
    CreatedBy(64L),
    DefinedAs(128L),
    DerivedFrom(256L),
    Desires(512L),
    DistinctFrom(1024L),
    Entails(2048L),
    EtymologicallyDerivedFrom(4096L),
    EtymologicallyRelatedTo(8192L),
    ExternalURL(16384L),
    FormOf(32768L),
    HasA(65536L),
    HasContext(131072L),
    HasFirstSubevent(262144L),
    HasLastSubevent(524288L),
    HasPrerequisite(1048576L),
    HasProperty(2097152L),
    HasSubevent(4194304L),
    InstanceOf(8388608L),
    IsA(16777216L),
    LocatedNear(33554432L),
    MadeOf(67108864L),
    MannerOf(134217728L),
    MotivatedByGoal(268435456),
    NotCapableOf(536870912L),
    NotDesires(1073741824L),
    NotHasProperty(2147483648L),
    NotUsedFor(4294967296L),
    ObstructedBy(8589934592L),
    PartOf(17179869184L),
    ReceivesAction(34359738368L),
    RelatedTo(68719476736L),
    SimilarTo(137438953472L),
    SymbolOf(274877906944L),
    Synonym(549755813888L),
    UsedFor(1099511627776L),
    capital(2199023255552L),
    field(4398046511104L),
    genre(8796093022208L),
    genus(17592186044416L),
    influencedBy(35184372088832L),
    knownFor(70368744177664L),
    language(140737488355328L),
    leader(281474976710656L),
    occupation(562949953421312L),
    product(1125899906842624L);

    public final long value;
    RelationshipTypes(long l) {
        this.value = l;
    }

    public CoarsenedHierarchicalType coarser() {
        HierarchicalTraverseEdgeType l = HierarchicalTraverse.createSemanticEdgeType(this);
        return SemanticEdge.coarser(true, l.type);
    }

    public boolean isSuitableForHierarchyTraversing() {
        switch (this) {
            case HasA:
            case HasContext:
            case HasFirstSubevent:
            case HasLastSubevent:
            case RelatedTo:
            case InstanceOf:
            case IsA:
            case MannerOf:
            case SimilarTo:
            case SymbolOf:
            case Synonym:
            case field:
            case genre:
            case genus:
                return true;
            default:
                return false;
        }
    }

    /**
     * Tests that the elements are correct power of twos
     * @param args
     */
    public static void main(String args[]) {
        long pow2 = 2;
        for (RelationshipTypes x : values()) {
            if (x.value != pow2)
                throw new RuntimeException("pow2 = "+pow2+" x.value="+x.value+" for name = "+x.name());
            pow2 = pow2 * 2L;
        }
    }
}
