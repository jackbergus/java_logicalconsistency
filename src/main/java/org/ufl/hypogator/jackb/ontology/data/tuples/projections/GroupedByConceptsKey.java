/*
 * GroupedByConceptsKey.java
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

package org.ufl.hypogator.jackb.ontology.data.tuples.projections;

import org.ufl.hypogator.jackb.disambiguation.dimension.concept.AbstractVocabulary;
import org.ufl.hypogator.jackb.ontology.data.HypoGatorResolvedMentionAssociation;
import org.ufl.hypogator.jackb.ontology.data.HypoGatorTrimmedMentions;

import java.util.List;

/**
 * This projection function is used to group by treeId, so that the data is grouped within the textual context of
 * interest.
 */
public class GroupedByConceptsKey extends EquivalenceObject {
    public String treeId;

    public GroupedByConceptsKey() {
        super();
    }

    public GroupedByConceptsKey(String treeId, AIDATuple.Key key, List<HypoGatorResolvedMentionAssociation> resolvedArguments, AbstractVocabulary<HypoGatorTrimmedMentions> av) {
        super(key, resolvedArguments, av);
        this.treeId = treeId;
    }

    public void setTreeId(String treeId) {
        this.treeId = treeId;
    }

    public void setKey(AIDATuple.Key key) {
        this.key = key;
    }

    public void setSat(boolean sat) {
        isSat = sat;
    }

    public void setAttributes(String attributes) {
        if (attributes.indexOf('¬') >= 0) {
            negation = true;
        } else {
            negation = false;
        }
        if (attributes.indexOf('~') >= 0) {
            uncertain = true;
        } else {
            uncertain = false;
        }
    }

    /**
     * Performs a projection, removing de facto the treeId. Therefore, the equivalence will be done
     * @return
     */
    public EquivalenceObject asEquivalenceObject(boolean ignoreSat) {
        return copy(ignoreSat);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        //GroupedByConceptsKey key1 = (GroupedByConceptsKey) o;
        return super.equals(o) &&                       //accessing the super arguments
                // TODO: ignoring the tree Id for the moment Objects.equals(treeId, key1.treeId);    //comparing the other argument
                true;
    }

    @Override
    public int hashCode() {
        return  super.hashCode();/*Objects.hash(treeId, super.hashCode());*/
    }

    public AIDATuple.Key getKey() {
        return key;
    }
}
