/*
 * Concepts.java
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

import org.ufl.hypogator.jackb.comparators.partialOrders.PartialOrderComparison;
import org.ufl.hypogator.jackb.disambiguation.dimension.Dimension;

import java.io.File;
import java.util.Objects;

public class DimConcepts extends Dimension<ResolvedConcept, InformativeConcept> {
    private final String dimension;
    private ComparingConceptResolution comparator;
    private DisambiguatorForDimensionForConcept disambiguator;

    protected DimConcepts(String dimension, ComparingConceptResolution comparator, DisambiguatorForDimensionForConcept disambiguator) {
        super(comparator.setDisabiguator(disambiguator), disambiguator);
        this.dimension = dimension;
        this.comparator = comparator;
        this.disambiguator = disambiguator;
        System.err.println("Loaded: Concept for " + dimension);
    }

    public DimConcepts(String dimension) {
        this(dimension, new ComparingConceptResolution(dimension), new DisambiguatorForDimensionForConcept(dimension, argumentsForPartof));
    }

    /**
     * This functions provides penalizations to terms that are not met (e.g., Union)
     * @param left
     * @param right
     * @return
     */
    @Override
    protected PartialOrderComparison nonNullCompare(String left, String right) {
        PartialOrderComparison cp = super.nonNullCompare(left, right);
        return dimension.equals("Union") ? new PartialOrderComparison(cp.t, cp.uncertainty * 0.7) : cp;
    }

    @Override
    public boolean equals(Object o) {
        return ((o instanceof DimConcepts) && Objects.equals(dimension, ((DimConcepts)o).dimension));
    }

    @Override
    public void serializeToDisk(File file) {
        comparator.serializeToDisk(file);
    }

    @Override
    public void loadFromDisk(File file) {
        comparator.loadFromDisk(file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dimension.hashCode());
    }

    @Override
    public String getName() {
        return dimension;
    }

    public void close() {
        disambiguator.close();
    }

    static String[] argumentsForPartof = new String[]{"partOf"};
    @Override
    public String[] allowedKBTypesForTypingExpansion() {
        return argumentsForPartof;
    }

    @Override
    public boolean allowReflexiveExpansion() {
        return false;
    }
}
