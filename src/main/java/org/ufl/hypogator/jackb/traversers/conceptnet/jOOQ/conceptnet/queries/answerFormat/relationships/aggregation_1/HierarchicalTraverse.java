/*
 * HierarchicalTraverse.java
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


import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.relationships.raw_type.RelationshipTypes;

public enum HierarchicalTraverse {
    /**
     * Related concepts
     */
    GenericConceptRelatedness,

    /**
     * Non-semnatic relation. It can be discarded from the reasoning process
     */
    None,

    /**
     * Representing aggregations. Inverse of HasContentInstance
     */
    PartOfIsA,

    /**
     * Connecting instance of elements to subistances. Inverse of PartOfIsA
     */
    HasContentInstance,

    /**
     * The source element affects the target, or causes the target
     */
    LeftToRightImplication,

    /**
     * The target element affects the source, or causes the source
     */
    RightToLeftImplication,

    /**
     * This case relates two similar concepts.
     */
    Equality;


    /**
     * Inverts the direction of the relation
     *
     * @return
     */
    public HierarchicalTraverse invertRelationDirection() {
        switch (this) {
            case PartOfIsA:
                return HasContentInstance;
            case HasContentInstance:
                return PartOfIsA;
            case LeftToRightImplication:
                return RightToLeftImplication;
            case RightToLeftImplication:
                return LeftToRightImplication;
            case Equality:
                return Equality;
            case GenericConceptRelatedness:
                return GenericConceptRelatedness;
            case None:
                return None;
        }
        return this;
    }

    public boolean isPreferredDirection() {
        switch (this) {
            case GenericConceptRelatedness:
            case None:
            case PartOfIsA:
            case LeftToRightImplication:
            case Equality:
                return true;
        }
        return false;
    }


    public static HierarchicalTraverseEdgeType createSemanticEdgeType(RelationshipTypes t) {
        switch (t) {
            case Antonym:
            case DistinctFrom:
                return new HierarchicalTraverseEdgeType(Equality, false);

            case AtLocation:
            case CapableOf:
            case LocatedNear:
            case SimilarTo:
            case RelatedTo:
                return new HierarchicalTraverseEdgeType(GenericConceptRelatedness, true);


            case Causes:
            case CausesDesire:
            case Desires:
            case HasProperty:
            case MadeOf:
            case Entails:
            case EtymologicallyRelatedTo:
            case DerivedFrom://
            case ObstructedBy://
            case EtymologicallyDerivedFrom://
            case occupation://
            case leader://
            case knownFor://
            case HasSubevent:
            case HasFirstSubevent:
            case HasLastSubevent:
                return new HierarchicalTraverseEdgeType(LeftToRightImplication, true);


            case CreatedBy:
            case HasPrerequisite:
            case influencedBy:
            case ReceivesAction:
            case UsedFor:
            case MotivatedByGoal:
                return new HierarchicalTraverseEdgeType(RightToLeftImplication, true);


            case DefinedAs:
            case Synonym:
                return new HierarchicalTraverseEdgeType(Equality, true);


            case PartOf:
            case IsA:
            case HasContext:
            case FormOf:
            case InstanceOf:
            case SymbolOf:
            case MannerOf:
            case genre:
            case genus:
            case field:
            case product://
                return new HierarchicalTraverseEdgeType(PartOfIsA, true);


            case language:
            case ExternalURL:
                return new HierarchicalTraverseEdgeType(None, true);


            case NotCapableOf:
                return new HierarchicalTraverseEdgeType(GenericConceptRelatedness, false);

            case NotDesires:
            case NotHasProperty:
                return new HierarchicalTraverseEdgeType(LeftToRightImplication, false);

            case NotUsedFor:
                return new HierarchicalTraverseEdgeType(RightToLeftImplication, false);

            case capital:
            case HasA:
                return new HierarchicalTraverseEdgeType(HasContentInstance, true);

        }
        return new HierarchicalTraverseEdgeType(None, false);
    }
}
