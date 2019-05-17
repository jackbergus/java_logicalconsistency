/*
 * EquivalenceObject.java
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
import org.ufl.hypogator.jackb.fuzzymatching.ldc.LDCResult;
import org.ufl.hypogator.jackb.ontology.data.HypoGatorResolvedMentionAssociation;
import org.ufl.hypogator.jackb.ontology.data.TypedValue;
import org.ufl.hypogator.jackb.ontology.data.HypoGatorTrimmedMentions;

import java.util.*;

/**
 * Implementation of the object's equivalence class for element merging
 */
public class EquivalenceObject {
    public AIDATuple.Key key;
    public Boolean isSat;
    public boolean negation;
    public boolean uncertain;
    public List<HypoGatorResolvedMentionAssociation> resolvedArguments;
    public List<String> resolvedArgumentsForDisplay;
    private AIDATuple candidate;

    public EquivalenceObject() {
    }

    public GroupedByConceptsKey asGroupedByConceptsKey() {
        GroupedByConceptsKey toret =  new GroupedByConceptsKey(null, key, Collections.emptyList(), candidate.abstractVocabulary);
        toret.negation = negation;
        toret.uncertain = uncertain;
        toret.isSat = isSat;
        return toret;
    }

    public EquivalenceObject copy() {
        return copy(false);
    }

    public EquivalenceObject copy(boolean ignoreSat) {
        EquivalenceObject toret = new EquivalenceObject();
        toret.key = key;
        if (!ignoreSat)
            toret.isSat = isSat;
        toret.negation = negation;
        toret.uncertain = uncertain;
        toret.resolvedArguments = resolvedArguments;
        toret.resolvedArgumentsForDisplay = resolvedArgumentsForDisplay;
        toret.candidate = candidate;
        return toret;
    }

    public EquivalenceObject(AIDATuple.Key key, List<HypoGatorResolvedMentionAssociation> resolvedArguments, AbstractVocabulary<HypoGatorTrimmedMentions> av) {
        this.negation = false;
        this.uncertain = false;
        this.key = key;
        this.isSat = null;
        this.resolvedArguments = resolvedArguments;
        resolvedArgumentsForDisplay = new ArrayList<>(resolvedArguments.size());

        for (int i = 0, resolvedArgumentsSize = resolvedArguments.size(); i < resolvedArgumentsSize; i++) {
            HypoGatorResolvedMentionAssociation x = resolvedArguments.get(i);
            TypedValue val = x.getValue();
            LDCResult resolved = null;
            if (val != null && (resolved = val.value()) != null)
                resolved = AIDATuple.parseForDisplay(resolved.resolved, av);
            if (resolved != null && resolved.nistType != null) {
                x.getValue().setTypeComingFromFuzzyMatch();
                x.getValue().setType(resolved.nistType);
            }
            resolvedArgumentsForDisplay.add(val == null || resolved == null ? null : resolved.resolved);
        }
    }

    public AIDATuple getCandidate() {
        return candidate;
    }

    public void setCandidate(AIDATuple candidate) {
        this.candidate = candidate;
    }


    public String attributes() {
        return this.negation ? "¬" : (this.uncertain ? "~" : "");
    }

    public void addAllSimilarMentionIds(Collection<String> similarMentionIds) {
        if (similarMentionIds != null && candidate != null)
            this.candidate.similarMentionIds.addAll(similarMentionIds);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquivalenceObject that = (EquivalenceObject) o;
        return negation == that.negation &&
                uncertain == that.uncertain &&
                Objects.equals(key, that.key) &&
                Objects.equals(isSat, that.isSat) &&
                Objects.equals(resolvedArgumentsForDisplay, that.resolvedArgumentsForDisplay);
    }

    @Override
    public String toString() {
        return "EquivalenceObject{" +
                "key=" + key +
                ", isSat=" + isSat +
                ", negation=" + negation +
                ", uncertain=" + uncertain +
                ", resolvedArgumentsForDisplay=" + resolvedArgumentsForDisplay +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(negation, uncertain, key, isSat, resolvedArgumentsForDisplay);
    }

}
