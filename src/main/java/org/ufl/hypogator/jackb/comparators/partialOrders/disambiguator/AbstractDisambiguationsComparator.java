/*
 * DisambiguationsComparator.java
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

package org.ufl.hypogator.jackb.comparators.partialOrders.disambiguator;

import com.google.common.collect.HashMultimap;
import javafx.util.Pair;
import org.ufl.hypogator.jackb.ConfigurationEntrypoint;
import org.ufl.hypogator.jackb.comparators.partialOrders.InformationPreservingComparator;
import org.ufl.hypogator.jackb.comparators.partialOrders.POCType;
import org.ufl.hypogator.jackb.comparators.partialOrders.PartialOrderComparison;
import org.ufl.hypogator.jackb.comparators.partialOrders.policy.DisambiguationPolicy;
import org.ufl.hypogator.jackb.comparators.partialOrders.policy.DisambiguationPolicyFactory;
import org.ufl.hypogator.jackb.disambiguation.DisambiguatedValue;
import org.ufl.hypogator.jackb.disambiguation.Resolved;
import org.ufl.hypogator.jackb.utils.adt.Triple;

import java.io.File;
import java.util.function.BiFunction;

/**
 * Given two disambiguated terms, provides the most probable
 *
 * @param <T>
 */
public abstract class AbstractDisambiguationsComparator<T extends Resolved, K extends DisambiguatedValue<T>> implements BiFunction<K, K, HashMultimap<POCType, Double>> {

    protected final InformationPreservingComparator<T> ambiguousComparator;

    /**
     * This class accepts a dimension for each element that can be disambiguated
     *
     * @param ambiguousComparator Comparator over ambiguous terms
     */
    public AbstractDisambiguationsComparator(InformationPreservingComparator<T> ambiguousComparator) {
        this.ambiguousComparator = ambiguousComparator;
    }


}