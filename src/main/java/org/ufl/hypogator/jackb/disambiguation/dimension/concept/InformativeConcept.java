/*
 * ConceptInformation.java
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

package org.ufl.hypogator.jackb.disambiguation.dimension.concept;

import javafx.util.Pair;
import org.ufl.hypogator.jackb.disambiguation.DisambiguatedValue;
import org.ufl.hypogator.jackb.utils.adt.Triple;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class InformativeConcept extends DisambiguatedValue<ResolvedConcept> implements Serializable {
    static final long serialVersionUID = 43L;
    private final String dimension;
    private final Pair<Double, List<String>> value;

    /**
     * @param matched   Original string that allowed the fuzzy (or perfect) match
     * @param dimension LegacyDimension that was used to reconcile the information
     * @param value     Hierarchy from the fuzzy match towards the hierarchy. Please note that the one provided
     *                  is only one of the all possible values
     */
    public InformativeConcept(String matched, String dimension, Pair<Double, List<String>> value) {
        super(matched);
        this.dimension = dimension;
        this.value = value;
    }

    public Double getScore() {
        return value.getKey();
    }

    public String getDisambiguated() {
        return value.getValue().isEmpty() ? getValue() : value.getValue().get(0);
    }

    public List<String> getDisambiguationPath() {
        return value.getValue();
    }

    @Override
    public List<String> pathFromDisambiguation(ResolvedConcept disambiguation) {
        return disambiguation.getDisambiguationPath();
    }

    @Override
    public String matchedString(ResolvedConcept disambiguation) {
        return disambiguation.getValue();
    }

    /**
     * Concepts can represent other dimensions as well.
     *
     * @return
     */
    @Override
    public String getType() {
        return dimension;
    }

    @Override
    public void setDisambiguations(Collection<ResolvedConcept> disambiguation) {

    }

    @Override
    public Collection<Triple<String, ResolvedConcept, Double>> getDisambiguation() {
        return this.disambiguation;
    }
}
