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

package org.ufl.hypogator.jackb.comparators.partialOrders;

import com.google.common.collect.HashMultimap;
import javafx.util.Pair;
import org.ufl.hypogator.jackb.ConfigurationEntrypoint;
import org.ufl.hypogator.jackb.comparators.partialOrders.policy.DisambiguationPolicy;
import org.ufl.hypogator.jackb.comparators.partialOrders.policy.DisambiguationPolicyFactory;
import org.ufl.hypogator.jackb.disambiguation.DisambiguatedValue;
import org.ufl.hypogator.jackb.disambiguation.Resolved;
import org.ufl.hypogator.jackb.utils.adt.Triple;

import java.io.File;

/**
 * Given two disambiguated terms, provides the most probable
 *
 * @param <T>
 */
public class DisambiguationsComparator<T extends Resolved, K extends DisambiguatedValue<T>> extends InformationPreservingComparator<K> {

    private final InformationPreservingComparator<T> ambiguousComparator;
    private final static DisambiguationPolicy dpf = DisambiguationPolicyFactory.getInstance().getPolicy(ConfigurationEntrypoint.getInstance().disambiguationPolicy);

    /**
     * This class accepts a dimension for each element that can be disambiguated
     *
     * @param ambiguousComparator Comparator over ambiguous terms
     */
    public DisambiguationsComparator(InformationPreservingComparator<T> ambiguousComparator) {
        this.ambiguousComparator = ambiguousComparator;
    }



    private static int compare(Pair<POCType, Double> o1, Pair<POCType, Double> o2) {
        int cmp = o1.getKey().compareTo(o2.getKey());
        if (cmp == 0) {
            return o1.getValue().compareTo(o2.getValue());
        } else {
            return cmp;
        }
    }

    /**
     * Actual comparison between disambiguated values
     *
     * @param left
     * @param right
     * @return
     */
    @Override
    protected PartialOrderComparison nonNullCompare(K left, K right) {
        if (left.str.equals("convoy") || right.str.equals("convoy"))
            System.err.println("BREAK");
        HashMultimap<POCType, Double> map = HashMultimap.create();
        /*if ((!left.getDisambiguation().isEmpty()) && (!right.getDisambiguation().isEmpty())) {
            Triple<String, T, Double> le = left.getDisambiguation().iterator().next();
            Triple<String, T, Double> re = right.getDisambiguation().iterator().next();
            double leS = Double.min(le.third, 1.0);
            double reS = Double.min(re.third, 1.0);
            double leSOrR = (leS * reS);
            PartialOrderComparison cmp = ambiguousComparator.compare(le.second, re.second);
            //if (cmp.t != PartialOrderComparison.Type.Uncomparable)
            map.put(cmp.t, cmp.uncertainty * leSOrR);
        }*/
        for (Triple<String, T, Double> le : left.getDisambiguation()) {
            double leS = Double.min(le.third, 1.0);
            for (Triple<String, T, Double> re : right.disambiguation) {
                double leSOrR = ((leS) * (Double.min(re.third, 1.0)));
                PartialOrderComparison cmp = ambiguousComparator.compare(le.second, re.second);
                //if (cmp.t != PartialOrderComparison.Type.Uncomparable)
                map.put(cmp.t, cmp.uncertainty * leSOrR);
            }
        }
        if (map.isEmpty()) { // If the map was not able to perfom a comparison (e.g., no possible disambiguation) then the two elements are automatically uncomparable
            return PartialOrderComparison.PERFECT_UNCOMPARABLE;
        } else {
            return dpf.getDirection(map);
        }
    }

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    public void serializeToDisk(File file) {
        ambiguousComparator.serializeToDisk(file);
    }

    @Override
    public void loadFromDisk(File file) {
        ambiguousComparator.loadFromDisk(file);
    }

    @Override
    public void close() {

    }
}
