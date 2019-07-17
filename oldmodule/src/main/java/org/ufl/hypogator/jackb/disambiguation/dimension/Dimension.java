/*
 * SubDimension.java
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

package org.ufl.hypogator.jackb.disambiguation.dimension;

import org.ufl.hypogator.jackb.comparators.partialOrders.DisambiguatorsWithApproximations;
import org.ufl.hypogator.jackb.comparators.partialOrders.InformationPreservingComparator;
import org.ufl.hypogator.jackb.disambiguation.DisambiguatedValue;
import org.ufl.hypogator.jackb.disambiguation.Resolved;
import org.ufl.hypogator.jackb.disambiguation.DisambiguatorForDimension;

public abstract class Dimension<T extends Resolved, K extends DisambiguatedValue<T>>

        extends DisambiguatorsWithApproximations<T, K> {

    public Dimension(InformationPreservingComparator<T> comparator, DisambiguatorForDimension<T, K> disambiguator) {
        super(comparator, disambiguator);
    }

    public abstract String[] allowedKBTypesForTypingExpansion();
}
