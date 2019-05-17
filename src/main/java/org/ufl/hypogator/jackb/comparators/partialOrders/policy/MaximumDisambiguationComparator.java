package org.ufl.hypogator.jackb.comparators.partialOrders.policy;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.TreeMultimap;
import org.ufl.hypogator.jackb.comparators.partialOrders.POCType;
import org.ufl.hypogator.jackb.comparators.partialOrders.PartialOrderComparison;

import java.util.Collection;
import java.util.Map;
import java.util.NavigableSet;

/**
 * This scoring takes into account only the maximum score of the overall inferred directions.
 */
public class MaximumDisambiguationComparator implements DisambiguationPolicy {

    @Override
    public PartialOrderComparison getDirection(HashMultimap<POCType, Double> map) {
        TreeMultimap<Double, POCType> tree =
                TreeMultimap.create();
        // Evaluating the score associated to each direction
        for (Map.Entry<POCType, Collection<Double>> s : map.asMap().entrySet()) {
            double finalScore = 1, average = 0;
            for (Double dv : s.getValue()) {
                finalScore *= (1 - dv);
                average += dv;
            }
            average = average / ((double) s.getValue().size());
            finalScore = (1 - finalScore) * average;
            tree.put(finalScore, s.getKey());
        }
        // If the maximum score is "very low", then there is no possible comparison
        double d = tree.keySet().stream().mapToDouble(x -> x).max().orElse(1);
        if (d < 0.01) {
            return PartialOrderComparison.PERFECT_UNCOMPARABLE;
        }

        NavigableSet<POCType> maxValues = tree.get(d);
            /*ArrayList<PartialOrderComparison.Type> al = new ArrayList<>();
            double finalScore = 0;
            for (PartialOrderComparison.Type y : max) {
                al.add(y.getKey());
                finalScore += y.getValue();
            }
            finalScore = finalScore / ((double)max.size());*/
        return new PartialOrderComparison(DisambiguationPolicy.typeAggregator(maxValues), d);
    }

    @Override
    public String getName() {
        return "MaximumDisambiguationComparator";
    }
}
