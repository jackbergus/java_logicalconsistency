/*
 * DisambiguatorsWithApproximations.java
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

import org.ufl.hypogator.jackb.ConfigurationEntrypoint;
import org.ufl.hypogator.jackb.disambiguation.DisambiguatedValue;
import org.ufl.hypogator.jackb.disambiguation.DisambiguationAlgorithm;
import org.ufl.hypogator.jackb.disambiguation.DisambiguatorForDimension;
import org.ufl.hypogator.jackb.disambiguation.Resolved;
import org.ufl.hypogator.jackb.fuzzymatching.Similarity;
import org.ufl.hypogator.jackb.fuzzymatching.SimilarityFactory;

import java.util.*;

import static org.ufl.hypogator.jackb.comparators.partialOrders.POCType.Equal;
import static org.ufl.hypogator.jackb.comparators.partialOrders.POCType.Uncomparable;


public abstract class DisambiguatorsWithApproximations<T extends Resolved,
        K extends DisambiguatedValue<T>>

        extends InformationPreservingComparator<String>

        implements DisambiguatorForDimension<T, K> {

    public final DisambiguationsComparator<T, K> disambiguatedComparator;
    public final DisambiguationAlgorithm<T, K> algorithm;
    private static final double threshold = ConfigurationEntrypoint.getInstance().threshold;
    private static final Similarity sim = SimilarityFactory.getDefaultSimilarity();
    private final HashMap<String, K> memoization;

    /**
     * Contains a set of terms which disambiguation is known to be mapped into a null string.
     *
     * TODO: extend to pronouns and subjects
     */
    private final Set<String> nonTerms;

    //public abstract Pair<Direction, Optional<Pair<Double, List<SemanticNetworkEntryPoint>>>> informativeCompare(T cpT, T cpU);

    public DisambiguatorsWithApproximations(InformationPreservingComparator<T> comparator,
                                            DisambiguatorForDimension<T, K> disambiguator) {

        this.disambiguatedComparator = comparator != null ? new DisambiguationsComparator<>(comparator) : null;
        this.algorithm = disambiguator != null ? disambiguator.getAlgorithm(threshold) : null;
        this.memoization = new HashMap<>();
        this.nonTerms = new HashSet<>();
    }

    /**
     * This procedure is done to reduce the operations requiring a disambiguation, thus boosting the whole process.
     *
     * @param term Term to be disambiguated
     * @return Result of the disambiguation process. Please note that, if no disambiguation is possible
     * or if the term is null, then null is returned
     */
    private K memoize(String term) {
        if (term == null)
            return null;
        if (nonTerms.contains(term)) {
            return null;
        } else {
            K dis = memoization.get(term);
            if (dis == null) {
                /*if (term.equals("maidan nezalezhnosti"))
                    System.err.println("DEBUG");*/
                dis = algorithm.checkDisambiguation(term);
                if (dis == null)
                    nonTerms.add(term);
            }
            return dis;
        }
    }


    @Override
    protected PartialOrderComparison nonNullCompare(String left, String right) {
        if (left.equals(right)) {
            return PartialOrderComparison.PERFECT_EQUAL;
        }
        K leftDis = memoize(left);
        K rightDis = memoize(right);
        PartialOrderComparison cmp = disambiguatedComparator.compare(leftDis, rightDis);
        if (cmp != null && !cmp.t.equals(Uncomparable)) {
            return cmp;
        } else {
            double approx = sim.sim(left, right);
            if (approx > threshold)
                return new PartialOrderComparison(Equal, approx);
            else
                return cmp == null ? PartialOrderComparison.PERFECT_UNCOMPARABLE : cmp;
        }
    }

    @Override
    public K disambiguate(String str) {
        return algorithm.disambiguate(str);
    }

    @Override
    public DisambiguationAlgorithm<T, K> getAlgorithm(double threshold) {
        return algorithm.getAlgorithm(threshold);
    }

}
